package io.foundy.dashboard.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun EmptyDivideContent(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .padding(vertical = 88.dp)
    ) {
        CamstudyText(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            style = CamstudyTheme.typography.titleLarge.copy(
                color = CamstudyTheme.colorScheme.systemUi03
            )
        )
    }
}

@Preview
@Composable
fun EmptyDivideContentPreview() {
    CamstudyTheme {
        EmptyDivideContent("기록이 존재하지 않습니다")
    }
}
