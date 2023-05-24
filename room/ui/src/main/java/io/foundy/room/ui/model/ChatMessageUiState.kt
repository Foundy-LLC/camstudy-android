package io.foundy.room.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import io.foundy.room.domain.ChatMessage
import kotlinx.datetime.LocalDateTime

sealed class ChatMessageUiState {

    data class User(
        val id: String,
        val authorId: String,
        val authorName: String,
        val content: String,
        val sentDateTime: LocalDateTime,
    ) : ChatMessageUiState()

    data class System(
        @StringRes private val stringRes: Int,
        private val stringArgs: List<String> = emptyList()
    ) : ChatMessageUiState() {
        val message: String
            @Composable
            @ReadOnlyComposable
            get() {
                return stringResource(id = stringRes, formatArgs = stringArgs.toTypedArray())
            }
    }
}

fun ChatMessage.toUiState() = ChatMessageUiState.User(
    id = id,
    authorId = authorId,
    authorName = authorName,
    content = content,
    sentDateTime = sentDateTime
)
