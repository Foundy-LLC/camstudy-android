package io.foundy.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
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
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null
) {
    val isDuplicatedInput = addedTags.contains(value)

    CamstudyTextField(
        modifier = Modifier
            .onKeyEvent {
                val lastAddedTag = addedTags.lastOrNull()
                if (it.key == Key.Backspace && value.isEmpty() && lastAddedTag != null) {
                    onRemove(lastAddedTag)
                }
                true
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
        supportingText = if (isDuplicatedInput) "이미 입력한 태그입니다" else supportingText
    )
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
