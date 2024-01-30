package io.found.feature.user.ui

import androidx.annotation.StringRes
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.model.User
import io.foundy.core.ui.UserMessage

sealed class UserProfileDialogUiState {

    object Loading : UserProfileDialogUiState()

    data class Success(
        val user: User,
        val isFriendActionLoading: Boolean = false,
        @StringRes val friendActionSuccessMessageRes: Int? = null,
        @StringRes val friendActionFailureMessageRes: Int? = null,
        val onRequestFriend: () -> Unit,
        val onAcceptFriend: () -> Unit,
        val onCancelFriendRequest: () -> Unit,
        val onCancelFriend: () -> Unit
    ) : UserProfileDialogUiState() {

        val enabledFriendActionButton: Boolean
            get() {
                return friendActionSuccessMessageRes == null &&
                    friendActionFailureMessageRes == null &&
                    !isFriendActionLoading
            }

        val friendActionMessageRes: Int?
            @StringRes
            get() {
                return friendActionSuccessMessageRes ?: friendActionFailureMessageRes
            }

        val friendActionLeadingIcon: CamstudyIcon?
            get() {
                if (friendActionFailureMessageRes != null) {
                    return CamstudyIcons.Error
                }
                if (friendActionSuccessMessageRes != null) {
                    return CamstudyIcons.Done
                }
                return null
            }
    }

    data class Failure(val message: UserMessage) : UserProfileDialogUiState()
}
