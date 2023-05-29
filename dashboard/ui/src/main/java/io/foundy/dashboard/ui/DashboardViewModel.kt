package io.foundy.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.crop.data.repository.CropRepository
import io.foundy.ranking.data.repository.RankingRepository
import io.foundy.room_list.data.repository.RoomListRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val cropRepository: CropRepository,
    private val rankingRepository: RankingRepository,
    private val roomListRepository: RoomListRepository
) : ViewModel(), ContainerHost<DashboardUiState, DashboardSideEffect> {

    override val container: Container<DashboardUiState, DashboardSideEffect> = container(
        DashboardUiState(
            fetchGrowingCrop = ::fetchGrowingCrop,
            onRefresh = ::refresh
        )
    )

    private lateinit var currentUserId: String

    init {
        viewModelScope.launch {
            currentUserId = requireNotNull(getCurrentUserIdUseCase()) {
                "현재 회원 아이디를 얻을 수 없습니다. 로그인 하지 않고 대시보드에 접근했습니다."
            }
            refresh()
        }
        viewModelScope.launch {
            cropRepository.currentUserGrowingCropFlow.collectLatest { growingCrop ->
                intent {
                    reduce {
                        state.copy(
                            growingCropUiState = GrowingCropUiState.Success(
                                growingCrop = growingCrop
                            )
                        )
                    }
                }
            }
        }
    }

    private fun refresh() {
        fetchUserRanking()
        fetchGrowingCrop()
        fetchRecentRooms()
        fetchRecommendedRooms()
    }

    private fun fetchUserRanking() = intent {
        reduce { state.copy(userRankingUiState = UserRankingUiState.Loading) }
        rankingRepository.getUserRanking(
            userId = currentUserId,
            isWeekly = true,
            organizationId = null
        ).onSuccess { userRanking ->
            reduce {
                state.copy(
                    userRankingUiState = UserRankingUiState.Success(userRanking = userRanking)
                )
            }
        }.onFailure {
            reduce {
                state.copy(
                    userRankingUiState = UserRankingUiState.Failure(message = it.message)
                )
            }
        }
    }

    private fun fetchGrowingCrop() = intent {
        reduce { state.copy(growingCropUiState = GrowingCropUiState.Loading) }
        cropRepository.getGrowingCrop(userId = currentUserId)
            .onFailure { throwable ->
                reduce {
                    state.copy(
                        growingCropUiState = GrowingCropUiState.Failure(throwable.message)
                    )
                }
            }
    }

    private fun fetchRecentRooms() = intent {
        reduce { state.copy(recentRoomsUiState = RecentRoomsUiState.Loading) }
        roomListRepository.getRecentRooms(userId = currentUserId)
            .onSuccess { rooms ->
                reduce {
                    state.copy(
                        recentRoomsUiState = RecentRoomsUiState.Success(
                            recentRooms = rooms
                        )
                    )
                }
            }.onFailure {
                reduce {
                    state.copy(
                        recentRoomsUiState = RecentRoomsUiState.Failure(message = it.message)
                    )
                }
            }
    }

    private fun fetchRecommendedRooms() = intent {
        reduce { state.copy(recommendedRoomsUiState = RecommendedRoomsUiState.Loading) }
        roomListRepository.getRecommendedRooms(userId = currentUserId)
            .onSuccess { rooms ->
                reduce {
                    state.copy(
                        recommendedRoomsUiState = RecommendedRoomsUiState.Success(
                            rooms = rooms
                        )
                    )
                }
            }.onFailure {
                reduce {
                    state.copy(
                        recommendedRoomsUiState = RecommendedRoomsUiState.Failure(
                            message = it.message
                        )
                    )
                }
            }
    }
}
