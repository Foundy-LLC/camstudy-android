package io.foundy.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme

private fun Char.isAddingAction(): Boolean {
    return this == ' ' || this == '\n'
}

// TODO: 태그 제거하는 버튼 추가하기
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TagInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    addedTags: List<String>,
    recommendedTags: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null
) {
    val isDuplicatedInput = addedTags.contains(value)
    val recommendListPopupState = rememberRecommendListPopupState(false)
    val shouldShowRecommendPopup = recommendListPopupState.isVisible && recommendedTags.isNotEmpty()

    Column {
        CamstudyTextField(
            modifier = modifier
                .onKeyEvent {
                    val lastAddedTag = addedTags.lastOrNull()
                    if (it.key == Key.Backspace && value.isEmpty() && lastAddedTag != null) {
                        onRemove(lastAddedTag)
                    }
                    false
                },
            value = value,
            onValueChange = { input ->
                val isAddAction = input.lastOrNull()?.isAddingAction() ?: false
                if (isAddAction) {
                    if (input.length == 1 || isDuplicatedInput) {
                        return@CamstudyTextField
                    }
                    onValueChange("")
                    onAdd(value)
                    recommendListPopupState.dismiss()
                    return@CamstudyTextField
                }
                onValueChange(input.filterNot { it.isAddingAction() })
                recommendListPopupState.show()
            },
            interactionSource = remember { MutableInteractionSource() }.apply {
                if (collectIsPressedAsState().value) {
                    recommendListPopupState.show()
                }
            },
            label = label,
            prefix = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AddedTags(tags = addedTags, onRemove = onRemove)
                    if (value.isNotEmpty()) {
                        CamstudyText(text = "#")
                    }
                }
            },
            singleLine = true,
            isError = isDuplicatedInput,
            placeholder = if (addedTags.isEmpty()) placeholder else null,
            supportingText = if (isDuplicatedInput) {
                stringResource(R.string.already_inputed_tag)
            } else {
                supportingText
            },
            borderShape = if (shouldShowRecommendPopup) {
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            } else {
                RoundedCornerShape(8.dp)
            },
            supportingContent = {
                if (shouldShowRecommendPopup) {
                    RecommendListPopup(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        items = recommendedTags,
                        state = recommendListPopupState,
                        onItemClick = { tag ->
                            if (addedTags.contains(tag)) {
                                error("Already input that tag!")
                            }
                            onValueChange("")
                            onAdd(tag)
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun AddedTags(tags: List<String>, onRemove: (String) -> Unit) {
    Row {
        for (tag in tags) {
            AddedTagItem(tag = "#$tag", onRemove = { onRemove(tag) })
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

@Composable
private fun AddedTagItem(tag: String, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color = CamstudyTheme.colorScheme.primary.copy(alpha = 0.4f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(6.dp))
        CamstudyText(modifier = Modifier.padding(vertical = 2.dp), text = tag)
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    onClick = onRemove,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ),
        ) {
            CamstudyIcon(
                modifier = Modifier.size(16.dp).align(Alignment.Center),
                icon = CamstudyIcons.Close,
                contentDescription = null,
                tint = CamstudyTheme.colorScheme.systemUi07
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagInputTextFieldPreview() {
    CamstudyTheme {
        TagInputTextField(
            value = "",
            onValueChange = {},
            addedTags = emptyList(),
            recommendedTags = emptyList(),
            onAdd = {},
            onRemove = {},
            label = "스터디 룸 태그",
            placeholder = "#수능 #개발",
            supportingText = "필수 입력 항목입니다"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddedTagPreview() {
    CamstudyTheme {
        TagInputTextField(
            value = "입력된 값",
            onValueChange = {},
            addedTags = listOf("개발", "공시"),
            recommendedTags = emptyList(),
            onAdd = {},
            onRemove = {},
            label = "스터디 룸 태그",
            placeholder = "#수능 #개발",
            supportingText = "필수 입력 항목입니다"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecommendTagPreview() {
    CamstudyTheme {
        TagInputTextField(
            value = "공",
            onValueChange = {},
            addedTags = listOf(),
            recommendedTags = listOf("공시", "공부"),
            onAdd = {},
            onRemove = {},
            label = "스터디 룸 태그",
            placeholder = "#수능 #개발",
            supportingText = "필수 입력 항목입니다"
        )
    }
}
