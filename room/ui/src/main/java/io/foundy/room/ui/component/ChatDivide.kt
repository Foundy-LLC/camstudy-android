package io.foundy.room.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.room.domain.ChatMessage
import io.foundy.room.ui.R

@Composable
fun ChatDivide(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    messages: List<ChatMessage>,
    chatInput: String,
    onChatInputChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onExpandClick: () -> Unit,
    onCollapseClick: () -> Unit
) {
    val colorScheme = CamstudyTheme.colorScheme

    Column(
        modifier.background(color = colorScheme.systemBackground)
    ) {
        ExpandableMessageHolder(
            modifier = Modifier.weight(1f),
            expanded = expanded,
            messages = messages,
            onExpandClick = onExpandClick,
            onCollapseClick = onCollapseClick
        )
        ChatInputBar(
            input = chatInput,
            onInputChange = onChatInputChange,
            onSendClick = onSendClick
        )
    }
}

@Composable
private fun ExpandableMessageHolder(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    messages: List<ChatMessage>,
    onExpandClick: () -> Unit,
    onCollapseClick: () -> Unit,
) {
    if (expanded) {
        ExpandedMessageHolder(
            modifier = modifier,
            messages = messages,
            onCollapseClick = onCollapseClick
        )
    } else {
        CollapsedMessageHolder(onExpandClick = onExpandClick)
    }
}

@Composable
private fun ExpandedMessageHolder(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    onCollapseClick: () -> Unit
) {
    val textStyle = CamstudyTheme.typography.titleSmall

    Box(modifier = modifier) {
        Row {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                reverseLayout = true
            ) {
                item {
                    Box(modifier = Modifier.height(12.dp))
                }
                items(items = messages, key = { it.id }) { message ->
                    Row(
                        modifier = Modifier.padding(start = 16.dp, end = 52.dp, top = 8.dp),
                    ) {
                        CamstudyText(
                            text = message.authorName,
                            style = textStyle.copy(color = CamstudyTheme.colorScheme.systemUi05)
                        )
                        Box(modifier = Modifier.width(8.dp))
                        CamstudyText(
                            text = message.content,
                            style = textStyle.copy(color = CamstudyTheme.colorScheme.systemUi09)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            CamstudyTheme.colorScheme.systemBackground,
                            CamstudyTheme.colorScheme.systemBackground.copy(alpha = 0.0f)
                        )
                    ),
                    alpha = 1f
                )
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 4.dp),
            onClick = onCollapseClick
        ) {
            CamstudyIcon(
                modifier = Modifier.size(24.dp),
                tint = CamstudyTheme.colorScheme.systemUi05,
                icon = CamstudyIcons.KeyboardArrowDown,
                contentDescription = null
            )
        }
    }
}

@Composable
fun CollapsedMessageHolder(onExpandClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CamstudyText(
            modifier = Modifier.weight(1f),
            text = "채팅을 보려면 우측 화살표를 눌러 펼처주세요",
            style = CamstudyTheme.typography.titleSmall.copy(
                color = CamstudyTheme.colorScheme.systemUi05
            )
        )
        IconButton(onClick = onExpandClick) {
            CamstudyIcon(
                modifier = Modifier.size(24.dp),
                icon = CamstudyIcons.KeyboardArrowUp,
                tint = CamstudyTheme.colorScheme.systemUi05,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    input: String,
    onInputChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
) {
    val enabledSendButton = input.isNotEmpty()
    val colorScheme = CamstudyTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = colorScheme.systemBackground
    ) {
        Box {
            CamstudyDivider()
            Row(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CamstudyTextField(
                    modifier = Modifier.weight(1f),
                    value = input,
                    placeholder = stringResource(R.string.chat_placeholder_text),
                    onValueChange = onInputChange
                )
                Box(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = { onSendClick(input) },
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

@Preview(heightDp = 300)
@Composable
private fun ExpandedChatDividePreview() {
    CamstudyTheme {
        ChatDivide(
            expanded = true,
            messages = listOf(
                ChatMessage(
                    id = "1",
                    authorId = "uid",
                    authorName = "홍길동",
                    content = "안녕하세요~!",
                    sentAt = "2022-05-08T19:57:12.123+09:00"
                )
            ),
            chatInput = "",
            onChatInputChange = {},
            onSendClick = {},
            onCollapseClick = {},
            onExpandClick = {}
        )
    }
}

@Preview
@Composable
private fun CollapsedChatDividePreview() {
    CamstudyTheme {
        ChatDivide(
            expanded = false,
            messages = listOf(
                ChatMessage(
                    id = "1",
                    authorId = "uid",
                    authorName = "홍길동",
                    content = "안녕하세요~!",
                    sentAt = "2022-05-08T19:57:12.123+09:00"
                )
            ),
            chatInput = "",
            onChatInputChange = {},
            onSendClick = {},
            onCollapseClick = {},
            onExpandClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
private fun ExpandedMessageHolderPreview() {
    val messages = List(100) {
        ChatMessage(
            id = it.toString(),
            authorId = "uid",
            authorName = "홍길동",
            content = "안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!",
            sentAt = "2022-05-08T19:57:12.123+09:00"
        )
    }
    CamstudyTheme {
        ExpandedMessageHolder(
            messages = messages,
            onCollapseClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CollapsedMessageHolderPreview() {
    CamstudyTheme {
        CollapsedMessageHolder(
            onExpandClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatInputBarPreview() {
    CamstudyTheme {
        ChatInputBar(
            input = "입력된 채팅",
            onInputChange = {},
            onSendClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyChatInputBarPreview() {
    CamstudyTheme {
        ChatInputBar(
            input = "",
            onInputChange = {},
            onSendClick = {}
        )
    }
}
