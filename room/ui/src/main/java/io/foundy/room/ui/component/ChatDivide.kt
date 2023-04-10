package io.foundy.room.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.room.ui.R

enum class ChatDivideState {
    Collapsed,
    HalfExpanded,
    Expanded
}

@Composable
fun ChatDivide(
    modifier: Modifier = Modifier,
    state: ChatDivideState = ChatDivideState.Collapsed,
    chatInput: String,
    onChatInputChange: (String) -> Unit,
    onSendClick: (String) -> Unit
) {
    val colorScheme = CamstudyTheme.colorScheme
    val enabledSendButton = chatInput.isNotEmpty()

    Surface(color = colorScheme.systemBackground) {
        Column(modifier = modifier.fillMaxWidth()) {
            Box {
                CamstudyDivider()
                Row(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CamstudyTextField(
                        modifier = Modifier.weight(1f),
                        value = chatInput,
                        placeholder = stringResource(R.string.chat_placeholder_text),
                        onValueChange = onChatInputChange
                    )
                    Box(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { onSendClick(chatInput) },
                        enabled = enabledSendButton,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = colorScheme.primary,
                            disabledContentColor = colorScheme.systemUi03
                        )
                    ) {
                        CamstudyIcon(
                            modifier = Modifier.size(36.dp),
                            icon = CamstudyIcons.Send,
                            contentDescription = stringResource(R.string.send_chat)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChatDividePreview() {
    CamstudyTheme {
        ChatDivide(
            chatInput = "입력된 채팅",
            onChatInputChange = {},
            onSendClick = {}
        )
    }
}

@Preview
@Composable
private fun EmptyChatDividePreview() {
    CamstudyTheme {
        ChatDivide(
            chatInput = "",
            onChatInputChange = {},
            onSendClick = {}
        )
    }
}
