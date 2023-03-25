package io.foundy.room.ui.viewmodel

import io.foundy.room.domain.ChatMessage

data class ChatUiState(
    val chatMessages: List<ChatMessage> = emptyList(),
    val messageInput: String = "",
    val onChangeMessageInput: (String) -> Unit,
    val onSendMessage: (String) -> Unit
) {
    val canSendButton: Boolean get() = messageInput.isNotEmpty()
}
