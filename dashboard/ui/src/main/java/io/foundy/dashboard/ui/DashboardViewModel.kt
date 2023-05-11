package io.foundy.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.crop.data.repository.CropRepository
import io.foundy.ranking.data.repository.RankingRepository
import io.foundy.room_list.data.repository.RoomListRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cropRepository: CropRepository,
    private val rankingRepository: RankingRepository,
    private val roomListRepository: RoomListRepository
) : ViewModel(), ContainerHost<DashboardUiState, DashboardSideEffect> {

    override val container: Container<DashboardUiState, DashboardSideEffect> = container(
        DashboardUiState(fetchGrowingCrop = ::fetchGrowingCrop)
    )

    private lateinit var currentUserId: String

    init {
        viewModelScope.launch {
            currentUserId = requireNotNull(authRepository.currentUserIdStream.firstOrNull()) {
                "현재 회원 아이디를 얻을 수 없습니다. 로그인 하지 않고 대시보드에 접근했습니다."
            }
            fetchUserRanking()
            fetchGrowingCrop()
            fetchRecentRooms()
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

    private fun fetchUserRanking() = intent {
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
}
