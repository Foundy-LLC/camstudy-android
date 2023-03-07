package io.foundy.room.ui.viewmodel

import androidx.annotation.StringRes
import com.example.domain.ChatMessage

sealed class RoomSideEffect {

    data class Message(
        val content: String?,
        @StringRes val defaultContentRes: Int
    ) : RoomSideEffect()

    data class OnChatMessage(
        val message: ChatMessage
    ) : RoomSideEffect()
}
