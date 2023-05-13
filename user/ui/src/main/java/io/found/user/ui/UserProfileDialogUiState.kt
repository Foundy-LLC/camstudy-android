package io.found.user.ui

import io.foundy.core.model.User
import io.foundy.core.ui.UserMessage

sealed class UserProfileDialogUiState {

    object Loading : UserProfileDialogUiState()

    data class Success(
        val user: User,
        val isFriendActionLoading: Boolean = false,
        val onRequestFriend: () -> Unit,
        val onCancelFriendRequest: () -> Unit,
        val onCancelFriend: () -> Unit
    ) : UserProfileDialogUiState()

    data class Failure(val message: UserMessage) : UserProfileDialogUiState()
}
