package io.foundy.room.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.room.domain.ChatMessage
import io.foundy.room.ui.R
import io.foundy.room.ui.viewmodel.ChatUiState
import io.foundy.room.ui.viewmodel.ChatViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ChatScreen(
    viewModel: ChatViewModel
) {
    val uiState = viewModel.collectAsState().value

    ChatContent(uiState = uiState)
}

@Composable
fun ChatContent(
    uiState: ChatUiState
) {
    val sendMessage = { uiState.onSendMessage(uiState.messageInput) }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f), reverseLayout = true) {
            // TODO: 새로운 메시지 도착시 최하단으로 스크롤하기
            items(
                items = uiState.chatMessages,
            ) { message ->
                ChatItem(chatMessage = message)
            }
        }
        CamstudyTextField(
            value = uiState.messageInput,
            onValueChange = uiState.onChangeMessageInput,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                sendMessage()
            }),
            trailingIcon = {
                IconButton(
                    enabled = uiState.canSendButton,
                    onClick = sendMessage
                ) {
                    CamstudyIcon(
                        icon = CamstudyIcons.Send,
                        contentDescription = stringResource(R.string.send_message)
                    )
                }
            }
        )
    }
}

@Composable
private fun ChatItem(
    chatMessage: ChatMessage
) {
    Row {
        Text(text = chatMessage.authorName)
        Text(text = chatMessage.content)
    }
}

@Composable
@Preview
private fun ChatContentPreview() {
    ChatContent(
        uiState = ChatUiState(
            messageInput = "안녕하세요",
            chatMessages = listOf(
                ChatMessage(
                    id = "id",
                    authorId = "김민성ID",
                    authorName = "김민성",
                    content = "만나서 반가워요",
                    sentAt = "2021-10-05T14:48:00.000Z"
                ),
                ChatMessage(
                    id = "id312",
                    authorId = "이리듐ID",
                    authorName = "이리듐",
                    content = "넵",
                    sentAt = "2021-10-05T14:49:00.000Z"
                )
            ),
            onSendMessage = {},
            onChangeMessageInput = {}
        )
    )
}
