package io.foundy.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Popup
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.component.CamstudyTextFieldTextStyle
import io.foundy.core.designsystem.theme.CamstudyTheme

private fun Char.isAddingAction(): Boolean {
    return this == ' ' || this == '\n'
}

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
                    return@CamstudyTextField
                }
                onValueChange(input.filterNot { it.isAddingAction() })
            },
            label = label,
            prefix = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AddedTags(tags = addedTags)
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
            borderShape = if (recommendedTags.isEmpty()) {
                RoundedCornerShape(8.dp)
            } else {
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            },
            supportingContent = {
                if (recommendedTags.isNotEmpty()) {
                    Box {
                        Popup {
                            RecommendedTags(
                                tags = recommendedTags,
                                onTagClick = { tag ->
                                    if (addedTags.contains(tag)) {
                                        error("Already input that tag!")
                                    }
                                    onValueChange("")
                                    onAdd(tag)
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun RecommendedTags(tags: List<String>, onTagClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            .background(color = CamstudyTheme.colorScheme.systemUi01)
    ) {
        for (tag in tags) {
            RecommendedTagItem(tag = tag, onClick = { onTagClick(tag) })
        }
    }
}

@Composable
private fun RecommendedTagItem(tag: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        CamstudyText(text = tag, style = CamstudyTextFieldTextStyle)
    }
}

@Composable
private fun AddedTags(tags: List<String>) {
    Row {
        for (tag in tags) {
            AddedTagItem(tag = "#$tag")
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

@Composable
private fun AddedTagItem(tag: String, modifier: Modifier = Modifier) {
    CamstudyText(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color = CamstudyTheme.colorScheme.primary.copy(alpha = 0.4f))
            .padding(vertical = 2.dp, horizontal = 6.dp),
        text = tag,
    )
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
