package io.foundy.feature.room.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.feature.room.ui.R
import io.foundy.feature.room.ui.model.ChatMessageUiState
import kotlinx.datetime.LocalDateTime
import kotlin.random.Random

private val ShadeOverlayHeight = 48.dp

@Composable
fun ChatDivide(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    messages: List<ChatMessageUiState>,
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
    messages: List<ChatMessageUiState>,
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
        CollapsedMessageHolder(
            lastMessage = messages.firstOrNull(),
            onExpandClick = onExpandClick
        )
    }
}

@Composable
private fun MessageContent(
    modifier: Modifier = Modifier,
    message: ChatMessageUiState,
    maxLines: Int = Int.MAX_VALUE
) {
    val textStyle = CamstudyTheme.typography.titleSmall

    when (message) {
        is ChatMessageUiState.System -> CamstudyText(
            modifier = modifier,
            text = message.message,
            style = textStyle.copy(color = CamstudyTheme.colorScheme.systemUi05)
        )
        is ChatMessageUiState.User -> Row(
            modifier = modifier,
        ) {
            CamstudyText(
                text = message.authorName,
                style = textStyle.copy(color = CamstudyTheme.colorScheme.systemUi05)
            )
            Box(modifier = Modifier.width(8.dp))
            CamstudyText(
                text = message.content,
                style = textStyle.copy(color = CamstudyTheme.colorScheme.systemUi09),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ExpandedMessageHolder(
    modifier: Modifier = Modifier,
    messages: List<ChatMessageUiState>,
    onCollapseClick: () -> Unit
) {
    Box(modifier = modifier) {
        if (messages.isEmpty()) {
            CamstudyText(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.there_is_no_message),
                style = CamstudyTheme.typography.titleLarge.copy(
                    color = CamstudyTheme.colorScheme.systemUi05
                )
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true
            ) {
                item {
                    Box(modifier = Modifier.height(12.dp))
                }
                items(items = messages) { message ->
                    MessageContent(
                        modifier = Modifier.padding(start = 16.dp, end = 52.dp, top = 8.dp),
                        message = message,
                    )
                }
                item {
                    Box(modifier = Modifier.height(ShadeOverlayHeight))
                }
            }
        }
        Box(
            modifier = Modifier
                .height(ShadeOverlayHeight)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            CamstudyTheme.colorScheme.systemBackground,
                            CamstudyTheme.colorScheme.systemBackground.copy(alpha = 0.0f)
                        )
                    )
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CollapsedMessageHolder(
    lastMessage: ChatMessageUiState?,
    onExpandClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
            targetState = lastMessage,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                createVerticalSlideAnimation().using(SizeTransform(clip = false))
            }
        ) { lastMessage ->
            if (lastMessage != null) {
                MessageContent(
                    maxLines = 1,
                    message = lastMessage,
                )
            } else {
                CamstudyText(
                    text = stringResource(R.string.collapsed_message_holder_help_text),
                    style = CamstudyTheme.typography.titleSmall.copy(
                        color = CamstudyTheme.colorScheme.systemUi05
                    )
                )
            }
        }
        Box(Modifier.width(8.dp))
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

@ExperimentalAnimationApi
private fun createVerticalSlideAnimation(duration: Int = 300): ContentTransform {
    return slideInVertically(animationSpec = tween(durationMillis = duration)) { height ->
        height
    } + fadeIn(
        animationSpec = tween(durationMillis = duration)
    ) with slideOutVertically(animationSpec = tween(durationMillis = duration)) { height ->
        -height
    } + fadeOut(
        animationSpec = tween(durationMillis = duration)
    )
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
                    maxLines = 3,
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
                ChatMessageUiState.User(
                    id = "1",
                    authorId = "uid",
                    authorName = "홍길동",
                    content = "안녕하세요~!",
                    sentDateTime = LocalDateTime.parse("2022-05-08T19:57:12.123+09:00")
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
    val messages = remember {
        mutableStateListOf(
            ChatMessageUiState.User(
                id = "1",
                authorId = "uid",
                authorName = "홍길동",
                content = "안녕하세요~!",
                sentDateTime = LocalDateTime.parse("2022-05-08T19:57:12.123+09:00")
            ),
            ChatMessageUiState.User(
                id = "2",
                authorId = "uid",
                authorName = "박민성",
                content = "네 반가워요.",
                sentDateTime = LocalDateTime.parse("2022-05-08T19:57:12.123+09:00")
            )
        )
    }
    CamstudyTheme {
        ChatDivide(
            expanded = false,
            messages = messages,
            chatInput = "wow",
            onChatInputChange = {},
            onSendClick = {
                messages.add(
                    index = 0,
                    element = ChatMessageUiState.User(
                        id = Random.nextInt().toString(),
                        authorId = "uid",
                        authorName = "박민성",
                        content = it,
                        sentDateTime = LocalDateTime.parse("2022-05-08T19:57:12.123+09:00")
                    )
                )
            },
            onCollapseClick = {},
            onExpandClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
private fun ExpandedMessageHolderPreview() {
    val messages = List(100) {
        ChatMessageUiState.User(
            id = it.toString(),
            authorId = "uid",
            authorName = "홍길동",
            content = "안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!안녕하세요~!",
            sentDateTime = LocalDateTime.parse("2022-05-08T19:57:12.123+09:00")
        )
    }
    CamstudyTheme {
        ExpandedMessageHolder(
            messages = messages,
            onCollapseClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
private fun EmptyExpandedMessageHolderPreview() {
    CamstudyTheme {
        ExpandedMessageHolder(
            messages = emptyList(),
            onCollapseClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CollapsedMessageHolderPreview() {
    CamstudyTheme {
        CollapsedMessageHolder(
            lastMessage = null,
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
