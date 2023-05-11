package io.foundy.ranking.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.model.OrganizationOverview
import io.foundy.core.ui.UserMessage
import io.foundy.organization.data.repository.OrganizationRepository
import io.foundy.ranking.data.repository.RankingRepository
import kotlinx.coroutines.flow.firstOrNull
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
    private val organizationRepository: OrganizationRepository
) : ViewModel(), ContainerHost<RankingUiState, RankingSideEffect> {

    override val container: Container<RankingUiState, RankingSideEffect> = container(
        RankingUiState(
            totalRanking = RankingTabUiState(
                fetchCurrentUserRanking = {
                    fetchCurrentUserRanking(RankingTabDestination.Total)
                }
            ),
            weeklyRanking = RankingTabUiState(
                fetchCurrentUserRanking = {
                    fetchCurrentUserRanking(RankingTabDestination.Weekly)
                }
            ),
            onSelectOrganization = ::updateSelectedOrganization
        )
    )

    init {
        intent {
            organizationRepository.getUserOrganizations(requireCurrentUserId())
                .onSuccess { organizations ->
                    reduce { state.copy(organizations = organizations) }
                }.onFailure {
                    postSideEffect(
                        RankingSideEffect.ErrorMessage(
                            message = UserMessage(
                                content = it.message,
                                defaultRes = R.string.failed_to_load_organizations_of_user
                            )
                        )
                    )
                }
        }
    }

    private suspend fun requireCurrentUserId(): String {
        return requireNotNull(authRepository.currentUserIdStream.firstOrNull())
    }

    private fun updateSelectedOrganization(organization: OrganizationOverview?) = intent {
        reduce { state.copy(selectedOrganization = organization) }
        RankingTabDestination.values.forEach {
            fetchCurrentUserRanking(it)
        }
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
                }
            }
        }
    }

    private fun fetchCurrentUserRanking(tabDestination: RankingTabDestination) {
        updateLoadingState(isLoading = true, tabDestination = tabDestination)
        intent {
            val organizationId = state.selectedOrganization?.id
            when (tabDestination) {
                RankingTabDestination.Total -> rankingRepository.getUserRanking(
                    userId = requireCurrentUserId(),
                    isWeekly = false,
                    organizationId = organizationId
                )
                RankingTabDestination.Weekly -> rankingRepository.getUserRanking(
                    userId = requireCurrentUserId(),
                    isWeekly = true,
                    organizationId = organizationId
                )
            }.onSuccess { currentUserRanking ->
                reduce {
                    when (tabDestination) {
                        RankingTabDestination.Total -> state.copy(
                            totalRanking = state.totalRanking.copy(
                                currentUserRanking = currentUserRanking,
                                rankingFlow = rankingRepository.getUserRankingList(
                                    organizationId = organizationId,
                                    isWeekly = false
                                ).cachedIn(viewModelScope)
                            )
                        )
                        RankingTabDestination.Weekly -> state.copy(
                            weeklyRanking = state.weeklyRanking.copy(
                                currentUserRanking = currentUserRanking,
                                rankingFlow = rankingRepository.getUserRankingList(
                                    organizationId = organizationId,
                                    isWeekly = true
                                ).cachedIn(viewModelScope)
                            )
                        )
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
}
