package io.foundy.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextFieldTextStyle
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun rememberRecommendListPopupState(initState: Boolean = false): RecommendListPopupState {
    return remember { RecommendListPopupState(initState) }
}

@Stable
class RecommendListPopupState(initState: Boolean = false) {

    var isVisible: Boolean by mutableStateOf(initState)
        private set

    fun show() {
        isVisible = true
    }

    fun dismiss() {
        isVisible = false
    }
}

@Composable
fun RecommendListPopup(
    items: List<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: RecommendListPopupState = rememberRecommendListPopupState(),
    shape: Shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
) {
    if (state.isVisible) {
        Box {
            Popup(
                onDismissRequest = state::dismiss
            ) {
                RecommendList(
                    items = items,
                    onItemClick = onItemClick,
                    modifier = modifier,
                    shape = shape
                )
            }
        }
    }
}

@Composable
private fun RecommendList(
    items: List<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(color = CamstudyTheme.colorScheme.systemUi01)
    ) {
        for (tag in items) {
            RecommendItem(item = tag, onClick = { onItemClick(tag) })
        }
    }
}

@Composable
private fun RecommendItem(item: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        CamstudyText(text = item, style = CamstudyTextFieldTextStyle)
    }
}

@Preview
@Composable
private fun RecommendListPopupPreview() {
    CamstudyTheme {
        RecommendListPopup(items = listOf("추천", "무엇"), onItemClick = {})
    }
}
