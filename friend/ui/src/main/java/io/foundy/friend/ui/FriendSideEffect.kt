package io.foundy.friend.ui

import androidx.annotation.StringRes

sealed class FriendSideEffect {

    object RefreshPagingData : FriendSideEffect()

    data class Message(
        val content: String? = null,
        @StringRes val defaultStringRes: Int
    ) : FriendSideEffect()
}
