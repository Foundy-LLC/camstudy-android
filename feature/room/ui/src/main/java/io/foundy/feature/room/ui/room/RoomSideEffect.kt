package io.foundy.feature.room.ui.room

import androidx.annotation.StringRes

sealed class RoomSideEffect {

    data class Message(
        val content: String? = null,
        @StringRes val defaultContentRes: Int,
        val stringResArgs: List<String> = emptyList()
    ) : RoomSideEffect()

    object Disconnected : RoomSideEffect()
}
