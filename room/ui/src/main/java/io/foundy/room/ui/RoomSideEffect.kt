package io.foundy.room.ui

import androidx.annotation.StringRes

sealed class RoomSideEffect {

    data class Message(
        val content: String?,
        @StringRes val defaultContentRes: Int
    ) : RoomSideEffect()
}
