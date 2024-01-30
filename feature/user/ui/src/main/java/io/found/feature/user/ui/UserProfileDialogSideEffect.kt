package io.found.feature.user.ui

import io.foundy.core.model.User

sealed class UserProfileDialogSideEffect {

    data class DidRequestFriend(val user: User) : UserProfileDialogSideEffect()

    data class DidRemoveFriend(val user: User) : UserProfileDialogSideEffect()

    data class DidCancelRequest(val user: User) : UserProfileDialogSideEffect()

    data class DidAcceptFriend(val user: User) : UserProfileDialogSideEffect()
}
