package io.foundy.room.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.room.data.model.StudyRoomEvent
import io.foundy.room.data.service.RoomService
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val roomService: RoomService
) : ViewModel(), ContainerHost<ChatUiState, ChatSideEffect> {

    override val container: Container<ChatUiState, ChatSideEffect> = container(
        ChatUiState(onChangeMessageInput = ::updateMessageInput, onSendMessage = ::sendMessage)
    )

    init {
        viewModelScope.launch {
            roomService.eventFlow.collect { event ->
                when (event) {
                    is StudyRoomEvent.OnReceiveChatMessage -> onReceiveChatMessage(event)
                    else -> {}
                }
            }
        }
    }

    fun bind(chatMessages: List<ChatMessage>) = intent {
        reduce { state.copy(chatMessages = chatMessages) }
    }

    private fun updateMessageInput(message: String) = intent {
        reduce { state.copy(messageInput = message) }
    }

    private fun sendMessage(message: String) = intent {
        if (!state.canSendButton) {
            return@intent
        }
        roomService.sendChat(message = message)
        reduce { state.copy(messageInput = "") }
    }

    private fun onReceiveChatMessage(event: StudyRoomEvent.OnReceiveChatMessage) = intent {
        reduce { state.copy(chatMessages = listOf(event.message) + state.chatMessages) }
    }
}
