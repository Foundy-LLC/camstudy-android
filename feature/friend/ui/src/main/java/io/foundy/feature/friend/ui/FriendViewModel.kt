package io.foundy.feature.friend.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.model.FriendStatus
import io.foundy.feature.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.feature.friend.data.repository.FriendRepository
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
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val friendRepository: FriendRepository
) : ViewModel(), ContainerHost<FriendUiState, FriendSideEffect> {

    override val container: Container<FriendUiState, FriendSideEffect> = container(
        FriendUiState(
            friendListTabUiState = FriendListTabUiState(),
            friendRecommendTabUiState = FriendRecommendTabUiState(
                fetchRecommendedUsers = ::fetchRecommendedFriends,
                onRequestFriend = ::requestFriend,
                onAcceptFriend = ::acceptFriendRequest,
                onCancelRequest = ::cancelFriendRequest,
                onRemoveFriend = ::removeFriend
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
            _currentUserId = getCurrentUserIdUseCase()
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

    private fun addPendingAtRecommendFriendUiState(userId: String) = intent {
        reduce {
            state.copy(
                friendRecommendTabUiState = state.friendRecommendTabUiState.copy(
                    inPendingUserIds = state.friendRecommendTabUiState.inPendingUserIds + userId
                )
            )
        }
    }

    private fun removePendingAtRecommendFriendUiState(userId: String) = intent {
        reduce {
            state.copy(
                friendRecommendTabUiState = state.friendRecommendTabUiState.copy(
                    inPendingUserIds = state.friendRecommendTabUiState.inPendingUserIds - userId
                )
            )
        }
    }

    fun changeRecommendTabFriendStatus(userId: String, status: FriendStatus) = intent {
        reduce {
            val friendRecommendTabUiState = state.friendRecommendTabUiState
            state.copy(
                friendRecommendTabUiState = friendRecommendTabUiState.copy(
                    recommendedUsers = friendRecommendTabUiState.recommendedUsers.map {
                        if (it.id == userId) {
                            return@map it.copy(friendStatus = status)
                        }
                        return@map it
                    }
                )
            )
        }
    }

    private fun acceptFriendRequest(requesterId: String) = intent {
        addPendingAtRequestedFriendUiState(requesterId)
        addPendingAtRecommendFriendUiState(requesterId)
        friendRepository.acceptFriendRequest(requesterId).onSuccess {
            changeRecommendTabFriendStatus(userId = requesterId, status = FriendStatus.ACCEPTED)
            postSideEffect(FriendSideEffect.RefreshFriendRequestingUserList)
            postSideEffect(FriendSideEffect.RefreshFriendList)
            postSideEffect(
                FriendSideEffect.Message(
                    defaultStringRes = R.string.accepted_friend
                )
            )
        }.onFailure {
            postSideEffect(
                FriendSideEffect.Message(
                    content = it.message,
                    defaultStringRes = R.string.failed_to_accept_request
                )
            )
        }
        removePendingAtRequestedFriendUiState(requesterId)
        removePendingAtRecommendFriendUiState(requesterId)
    }

    private fun requestFriend(userId: String) = intent {
        addPendingAtRecommendFriendUiState(userId)
        friendRepository.requestFriend(targetUserId = userId).onSuccess {
            changeRecommendTabFriendStatus(userId = userId, status = FriendStatus.REQUESTED)
            postSideEffect(
                FriendSideEffect.Message(
                    defaultStringRes = R.string.success_to_request_friend
                )
            )
        }.onFailure {
            postSideEffect(
                FriendSideEffect.Message(
                    content = it.message,
                    defaultStringRes = R.string.failed_to_request_friend
                )
            )
        }
        removePendingAtRecommendFriendUiState(userId)
    }

    private fun cancelFriendRequest(userId: String) = intent {
        addPendingAtRecommendFriendUiState(userId)
        friendRepository.deleteFriend(targetUserId = userId).onSuccess {
            changeRecommendTabFriendStatus(userId = userId, status = FriendStatus.NONE)
            postSideEffect(
                FriendSideEffect.Message(
                    defaultStringRes = R.string.canceled_friend_request
                )
            )
        }.onFailure {
            postSideEffect(
                FriendSideEffect.Message(
                    content = it.message,
                    defaultStringRes = R.string.failed_to_cancel_friend_request
                )
            )
        }
        removePendingAtRecommendFriendUiState(userId)
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

    private fun removeFriend(targetUserId: String) = intent {
        addPendingAtRecommendFriendUiState(targetUserId)
        friendRepository.rejectFriendRequest(targetUserId)
            .onSuccess {
                changeRecommendTabFriendStatus(userId = targetUserId, status = FriendStatus.NONE)
                postSideEffect(FriendSideEffect.RefreshFriendList)
                postSideEffect(
                    FriendSideEffect.Message(
                        defaultStringRes = R.string.removed_friend
                    )
                )
            }.onFailure {
                postSideEffect(
                    FriendSideEffect.Message(
                        content = it.message,
                        defaultStringRes = R.string.failed_to_remove_friend
                    )
                )
            }
        removePendingAtRecommendFriendUiState(targetUserId)
    }
}
