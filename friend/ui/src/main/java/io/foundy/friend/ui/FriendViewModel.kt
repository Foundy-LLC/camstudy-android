package io.foundy.friend.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
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
            friendListTabUiState = FriendListTabUiState(
                onRemoveFriendClick = ::deleteFriend,
            ),
            friendRecommendTabUiState = FriendRecommendTabUiState(
                onRequestFriend = { /* TODO */ }
            ),
            requestedFriendTabUiState = RequestedFriendTabUiState(
                onAcceptClick = ::acceptFriendRequest,
                onRejectClick = { /* TODO */ }
            )
        )
    )

    init {
        viewModelScope.launch {
            val currentUserId = authRepository.currentUserIdStream.firstOrNull()
            check(currentUserId != null)
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

    private fun acceptFriendRequest(requesterId: String) = intent {
        reduce {
            val requestedFriendUiState = state.requestedFriendTabUiState
            state.copy(
                requestedFriendTabUiState = requestedFriendUiState.copy(
                    inPendingUserIds = requestedFriendUiState.inPendingUserIds + requesterId
                )
            )
        }
        friendRepository.acceptFriendRequest(requesterId)
            .onSuccess {
                // TODO: postSideEffect(FriendSideEffect.RefreshPagingData)
            }.onFailure {
                postSideEffect(
                    FriendSideEffect.Message(
                        content = it.message,
                        defaultStringRes = R.string.failed_to_accept_request
                    )
                )
            }
        reduce {
            val requestedFriendUiState = state.requestedFriendTabUiState
            state.copy(
                requestedFriendTabUiState = requestedFriendUiState.copy(
                    inPendingUserIds = requestedFriendUiState.inPendingUserIds - requesterId
                )
            )
        }
    }

    private fun deleteFriend(targetUserId: String) = intent {
        reduce {
            val friendListTabUiState = state.friendListTabUiState
            state.copy(
                friendListTabUiState = friendListTabUiState.copy(
                    inRemovingUserIds = friendListTabUiState.inRemovingUserIds + targetUserId
                )
            )
        }
        friendRepository.deleteFriend(targetUserId)
            .onSuccess {
                postSideEffect(FriendSideEffect.RefreshFriendList)
                postSideEffect(
                    FriendSideEffect.Message(
                        defaultStringRes = R.string.dissmissed_friend
                    )
                )
            }.onFailure {
                postSideEffect(
                    FriendSideEffect.Message(
                        content = it.message,
                        defaultStringRes = R.string.failed_to_cancel_friend
                    )
                )
            }
        reduce {
            val friendListTabUiState = state.friendListTabUiState
            state.copy(
                friendListTabUiState = friendListTabUiState.copy(
                    inRemovingUserIds = friendListTabUiState.inRemovingUserIds - targetUserId
                )
            )
        }
    }
}
