package io.foundy.friend.ui

import androidx.annotation.StringRes

sealed class FriendSideEffect {

    data class Message(
        val content: String? = null,
        @StringRes val defaultStringRes: Int
    ) : FriendSideEffect()
}
