package io.foundy.ranking.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.ui.UserMessage
import io.foundy.ranking.data.repository.RankingRepository
import io.foundy.user.domain.usecase.GetUserUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val rankingRepository: RankingRepository,
    private val getUserUseCase: GetUserUseCase
) : ViewModel(), ContainerHost<RankingUiState, RankingSideEffect> {

    override val container: Container<RankingUiState, RankingSideEffect> = container(
        RankingUiState(
            totalRanking = RankingTabUiState(
                rankingFlow = rankingRepository.getUserRankingList(
                    organizationId = null,
                    isWeekly = false
                ).cachedIn(viewModelScope),
                fetchCurrentUserRanking = {
                    fetchCurrentUserRankingIfNull(RankingTabDestination.Total)
                }
            ),
            weeklyRanking = RankingTabUiState(
                rankingFlow = rankingRepository.getUserRankingList(
                    organizationId = null,
                    isWeekly = true
                ).cachedIn(viewModelScope),
                fetchCurrentUserRanking = {
                    fetchCurrentUserRankingIfNull(RankingTabDestination.Weekly)
                }
            ),
            onClickUser = ::fetchUser
        )
    )

    private var userFetchJob: Job? = null

    private suspend fun requireCurrentUserId(): String {
        return requireNotNull(authRepository.currentUserIdStream.firstOrNull())
    }

    private fun updateLoadingState(isLoading: Boolean, tabDestination: RankingTabDestination) {
        intent {
            reduce {
                when (tabDestination) {
                    RankingTabDestination.Total -> state.copy(
                        totalRanking = state.totalRanking.copy(
                            isCurrentUserRankingLoading = isLoading
                        )
                    )
                    RankingTabDestination.Weekly -> state.copy(
                        weeklyRanking = state.weeklyRanking.copy(
                            isCurrentUserRankingLoading = isLoading
                        )
                    )
                    RankingTabDestination.Organization -> TODO()
                }
            }
        }
    }

    private fun fetchCurrentUserRankingIfNull(tabDestination: RankingTabDestination) {
        updateLoadingState(isLoading = true, tabDestination = tabDestination)
        intent {
            when (tabDestination) {
                RankingTabDestination.Total -> rankingRepository.getUserRanking(
                    userId = requireCurrentUserId(),
                    isWeekly = false
                )
                RankingTabDestination.Weekly -> rankingRepository.getUserRanking(
                    userId = requireCurrentUserId(),
                    isWeekly = true
                )
                RankingTabDestination.Organization -> TODO()
            }.onSuccess { currentUserRanking ->
                reduce {
                    when (tabDestination) {
                        RankingTabDestination.Total -> state.copy(
                            totalRanking = state.totalRanking.copy(
                                currentUserRanking = currentUserRanking
                            )
                        )
                        RankingTabDestination.Weekly -> state.copy(
                            weeklyRanking = state.weeklyRanking.copy(
                                currentUserRanking = currentUserRanking
                            )
                        )
                        RankingTabDestination.Organization -> TODO()
                    }
                }
            }.onFailure {
                val message = UserMessage(
                    content = it.message,
                    defaultRes = R.string.failed_to_load_my_ranking
                )
                postSideEffect(RankingSideEffect.ErrorMessage(message = message))
            }
        }
        updateLoadingState(isLoading = false, tabDestination = tabDestination)
    }

    private fun fetchUser(id: String) = intent {
        if (state.userToShowDialog?.id == id) {
            return@intent
        }
        reduce { state.copy(userToShowDialog = null) }
        userFetchJob?.cancel()
        userFetchJob = viewModelScope.launch {
            getUserUseCase(id)
                .onSuccess { user ->
                    reduce {
                        state.copy(userToShowDialog = user)
                    }
                }.onFailure {
                    postSideEffect(RankingSideEffect.HideUserProfileDialog)
                    postSideEffect(
                        RankingSideEffect.ErrorMessage(
                            UserMessage(
                                content = it.message,
                                defaultRes = R.string.failed_to_load_user
                            )
                        )
                    )
                }
        }
    }
}
