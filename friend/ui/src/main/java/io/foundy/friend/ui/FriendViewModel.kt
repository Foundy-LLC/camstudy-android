package io.foundy.friend.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.friend.data.repository.FriendRepository
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel(), ContainerHost<FriendUiState, FriendSideEffect> {

    private var didBind = false

    override val container: Container<FriendUiState, FriendSideEffect> = container(
        FriendUiState(
            onAcceptClick = ::acceptFriendRequest,
            onRemoveFriendClick = ::deleteFriend
        )
    )

    fun bind(userId: String) = intent {
        if (didBind) {
            return@intent
        }
        didBind = true
        reduce {
            state.copy(
                friendPagingData = friendRepository.getFriends(userId = userId)
                    .cachedIn(viewModelScope),
                friendRequestPagingData = friendRepository.getFriendRequests(userId = userId)
                    .cachedIn(viewModelScope)
            )
        }
    }

    private fun addPendingUserId(userId: String) = intent {
        reduce { state.copy(inPendingUserIds = state.inPendingUserIds + userId) }
    }

    private fun removePendingUserId(userId: String) = intent {
        reduce { state.copy(inPendingUserIds = state.inPendingUserIds.filterNot { it == userId }) }
    }

    private fun acceptFriendRequest(requesterId: String) = intent {
        addPendingUserId(requesterId)
        friendRepository.acceptFriendRequest(requesterId)
            .onSuccess {
                postSideEffect(FriendSideEffect.RefreshPagingData)
            }
            .onFailure {
                postSideEffect(
                    FriendSideEffect.Message(
                        content = it.message,
                        defaultStringRes = R.string.failed_to_accept_request
                    )
                )
            }
        removePendingUserId(requesterId)
    }

    private fun deleteFriend(targetUserId: String) = intent {
        addPendingUserId(targetUserId)
        friendRepository.deleteFriend(targetUserId)
            .onSuccess {
                postSideEffect(FriendSideEffect.RefreshPagingData)
            }
            .onFailure {
                postSideEffect(
                    FriendSideEffect.Message(
                        content = it.message,
                        defaultStringRes = R.string.failed_to_cancel_friend
                    )
                )
            }
        removePendingUserId(targetUserId)
    }
}
