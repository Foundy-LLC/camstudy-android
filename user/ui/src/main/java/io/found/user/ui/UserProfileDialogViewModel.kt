package io.found.user.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.model.FriendStatus
import io.foundy.core.ui.UserMessage
import io.foundy.friend.data.repository.FriendRepository
import io.foundy.user.domain.usecase.GetUserUseCase
import io.foundy.user.ui.R
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class UserProfileDialogViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val friendRepository: FriendRepository
) : ViewModel(), ContainerHost<UserProfileDialogUiState, Unit> {

    override val container: Container<UserProfileDialogUiState, Unit> =
        container(UserProfileDialogUiState.Loading)

    fun fetchUser(id: String) = intent {
        val fetchedUser = (state as? UserProfileDialogUiState.Success)?.user
        if (fetchedUser?.id == id) {
            return@intent
        }
        reduce { UserProfileDialogUiState.Loading }
        getUserUseCase(id)
            .onSuccess { user ->
                reduce {
                    UserProfileDialogUiState.Success(
                        user = user,
                        onRequestFriend = ::requestFriend,
                        onCancelFriendRequest = ::cancelRequest,
                        onCancelFriend = ::cancelFriend
                    )
                }
            }.onFailure {
                reduce {
                    UserProfileDialogUiState.Failure(
                        message = UserMessage(
                            content = it.message,
                            defaultRes = R.string.user_dialog_failed_to_load_user
                        )
                    )
                }
            }
    }

    private fun requestFriend() = intent {
        val uiState = state
        check(uiState is UserProfileDialogUiState.Success)
        reduce { uiState.copy(isFriendActionLoading = true) }
        friendRepository.requestFriend(targetUserId = uiState.user.id)
            .onSuccess {
                reduce {
                    uiState.copy(
                        user = uiState.user.copy(friendStatus = FriendStatus.REQUESTED),
                        isFriendActionLoading = false
                    )
                }
                // TODO: show toast message or snackbar
            }.onFailure {
                // TODO: Show error message
                reduce { uiState.copy(isFriendActionLoading = false) }
            }
    }

    private fun cancelRequest() = intent {
        val uiState = state
        check(uiState is UserProfileDialogUiState.Success)
        reduce { uiState.copy(isFriendActionLoading = true) }
        friendRepository.deleteFriend(targetUserId = uiState.user.id)
            .onSuccess {
                reduce {
                    uiState.copy(
                        user = uiState.user.copy(friendStatus = FriendStatus.NONE),
                        isFriendActionLoading = false
                    )
                }
                // TODO: show toast message or snackbar
            }.onFailure {
                // TODO: Show error message
                reduce { uiState.copy(isFriendActionLoading = false) }
            }
    }

    private fun cancelFriend() = intent {
        val uiState = state
        check(uiState is UserProfileDialogUiState.Success)
        reduce { uiState.copy(isFriendActionLoading = true) }
        friendRepository.deleteFriend(targetUserId = uiState.user.id)
            .onSuccess {
                reduce {
                    uiState.copy(
                        user = uiState.user.copy(friendStatus = FriendStatus.NONE),
                        isFriendActionLoading = false
                    )
                }
                // TODO: show toast message or snackbar
            }.onFailure {
                // TODO: Show error message
                reduce { uiState.copy(isFriendActionLoading = false) }
            }
    }
}
