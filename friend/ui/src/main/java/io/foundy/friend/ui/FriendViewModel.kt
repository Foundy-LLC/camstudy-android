package io.foundy.friend.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.model.FriendStatus
import io.foundy.friend.data.repository.FriendRepository
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
class FriendViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val friendRepository: FriendRepository
) : ViewModel(), ContainerHost<FriendUiState, FriendSideEffect> {

    override val container: Container<FriendUiState, FriendSideEffect> = container(
        FriendUiState(
            friendListTabUiState = FriendListTabUiState(),
            friendRecommendTabUiState = FriendRecommendTabUiState(
                onRequestFriend = ::requestFriend,
                fetchRecommendedUsers = ::fetchRecommendedFriends
            ),
            requestedFriendTabUiState = RequestedFriendTabUiState(
                onAcceptClick = ::acceptFriendRequest,
                onRejectClick = ::rejectFriendRequest
            )
        )
    )

    private var _currentUserId: String? = null
    private val currentUserId: String get() = requireNotNull(_currentUserId)

    init {
        viewModelScope.launch {
            _currentUserId = authRepository.currentUserIdStream.firstOrNull()
            checkNotNull(_currentUserId)
            fetchRecommendedFriends()
            intent {
                reduce {
                    state.copy(
                        friendListTabUiState = state.friendListTabUiState.copy(
                            friendPagingData = friendRepository
                                .getFriends(userId = currentUserId)
                                .cachedIn(viewModelScope)
                        ),
                        requestedFriendTabUiState = state.requestedFriendTabUiState.copy(
                            requesterPagingData = friendRepository
                                .getFriendRequests(userId = currentUserId)
                                .cachedIn(viewModelScope)
                        )
                    )
                }
            }
        }
    }

    private fun fetchRecommendedFriends() = intent {
        reduce {
            state.copy(
                friendRecommendTabUiState = state.friendRecommendTabUiState.copy(isLoading = true)
            )
        }
        friendRepository.getRecommendedFriends(userId = currentUserId)
            .onSuccess { users ->
                reduce {
                    state.copy(
                        friendRecommendTabUiState = state.friendRecommendTabUiState.copy(
                            recommendedUsers = users
                        )
                    )
                }
            }.onFailure {
                postSideEffect(
                    FriendSideEffect.Message(
                        content = it.message,
                        defaultStringRes = R.string.failed_to_load_recommeded_friends
                    )
                )
            }
        reduce {
            state.copy(
                friendRecommendTabUiState = state.friendRecommendTabUiState.copy(isLoading = false)
            )
        }
    }

    private fun addPendingAtRequestedFriendUiState(pendingUserId: String) = intent {
        reduce {
            val requestedFriendUiState = state.requestedFriendTabUiState
            state.copy(
                requestedFriendTabUiState = requestedFriendUiState.copy(
                    inPendingUserIds = requestedFriendUiState.inPendingUserIds + pendingUserId
                )
            )
        }
    }

    private fun removePendingAtRequestedFriendUiState(pendingUserId: String) = intent {
        reduce {
            val requestedFriendUiState = state.requestedFriendTabUiState
            state.copy(
                requestedFriendTabUiState = requestedFriendUiState.copy(
                    inPendingUserIds = requestedFriendUiState.inPendingUserIds - pendingUserId
                )
            )
        }
    }

    private fun acceptFriendRequest(requesterId: String) = intent {
        addPendingAtRequestedFriendUiState(requesterId)
        friendRepository.acceptFriendRequest(requesterId).onSuccess {
            postSideEffect(FriendSideEffect.RefreshFriendRequestingUserList)
            postSideEffect(FriendSideEffect.RefreshFriendList)
            postSideEffect(
                FriendSideEffect.Message(
                    defaultStringRes = R.string.accepted_friend
                )
            )
            removeRecommendedUser(userId = requesterId)
        }.onFailure {
            postSideEffect(
                FriendSideEffect.Message(
                    content = it.message,
                    defaultStringRes = R.string.failed_to_accept_request
                )
            )
        }
        removePendingAtRequestedFriendUiState(requesterId)
    }

    // TODO: 함수 제거하거나 친구 상태 바꾸는 함수로 수정하기.
    //  친구 수락 or 친구 요청한 경우 추천 친구 목록에서 삭제하지 않고 친구 해제 버튼으로 둬야함
    fun removeRecommendedUser(userId: String) = intent {
        reduce {
            state.copy(
                friendRecommendTabUiState = state.friendRecommendTabUiState.copy(
                    recommendedUsers = state.friendRecommendTabUiState.recommendedUsers.filterNot {
                        it.id == userId
                    }
                )
            )
        }
    }

    private fun requestFriend(userId: String) = intent {
        reduce {
            state.copy(
                friendRecommendTabUiState = state.friendRecommendTabUiState.copy(
                    inPendingUserIds = state.friendRecommendTabUiState.inPendingUserIds + userId
                )
            )
        }
        friendRepository.requestFriend(targetUserId = userId).onSuccess { status ->
            reduce {
                val friendRecommendTabUiState = state.friendRecommendTabUiState
                state.copy(
                    friendRecommendTabUiState = friendRecommendTabUiState.copy(
                        recommendedUsers = friendRecommendTabUiState.recommendedUsers.filterNot {
                            it.id == userId
                        }
                    )
                )
            }
            when (status) {
                FriendStatus.REQUESTED -> {
                    postSideEffect(
                        FriendSideEffect.Message(
                            defaultStringRes = R.string.success_to_request_friend
                        )
                    )
                }
                FriendStatus.ACCEPTED -> {
                    postSideEffect(
                        FriendSideEffect.Message(
                            defaultStringRes = R.string.did_be_friend_because_there_is_request
                        )
                    )
                    postSideEffect(FriendSideEffect.RefreshFriendList)
                    postSideEffect(FriendSideEffect.RefreshFriendRequestingUserList)
                }
                else -> error("잘못된 친구 요청 응답 상태가 도착했습니다. 요청되었거나 친구가 되어야 합니다.")
            }
        }.onFailure {
            postSideEffect(
                FriendSideEffect.Message(
                    content = it.message,
                    defaultStringRes = R.string.failed_to_request_friend
                )
            )
        }
        reduce {
            state.copy(
                friendRecommendTabUiState = state.friendRecommendTabUiState.copy(
                    inPendingUserIds = state.friendRecommendTabUiState.inPendingUserIds - userId
                )
            )
        }
    }

    private fun rejectFriendRequest(requesterId: String) = intent {
        addPendingAtRequestedFriendUiState(requesterId)
        friendRepository.rejectFriendRequest(requesterId)
            .onSuccess {
                postSideEffect(FriendSideEffect.RefreshFriendRequestingUserList)
                postSideEffect(
                    FriendSideEffect.Message(
                        defaultStringRes = R.string.rejected_friend_request
                    )
                )
            }.onFailure {
                postSideEffect(
                    FriendSideEffect.Message(
                        content = it.message,
                        defaultStringRes = R.string.failed_to_reject_friend_request
                    )
                )
            }
        removePendingAtRequestedFriendUiState(requesterId)
    }
}
