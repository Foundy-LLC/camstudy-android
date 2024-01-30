package io.foundy.feature.friend.ui

import androidx.annotation.StringRes

sealed class FriendSideEffect {

    object RefreshFriendList : FriendSideEffect()

    object RefreshFriendRequestingUserList : FriendSideEffect()

    data class Message(
        val content: String? = null,
        @StringRes val defaultStringRes: Int
    ) : FriendSideEffect()
}
