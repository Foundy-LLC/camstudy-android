package io.found.user.ui

import io.foundy.core.model.User

sealed class UserProfileDialogSideEffect {

    data class DidRequestFriend(val user: User) : UserProfileDialogSideEffect()
}
