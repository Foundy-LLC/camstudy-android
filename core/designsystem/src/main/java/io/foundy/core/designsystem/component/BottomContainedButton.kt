package io.foundy.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

val BottomContainedButtonBoxHeight = 72.dp

@Composable
fun BoxScope.BottomContainedButton(
    enabled: Boolean = true,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(BottomContainedButtonBoxHeight)
            .align(Alignment.BottomCenter)
            .background(color = CamstudyTheme.colorScheme.systemBackground)
    ) {
        CamstudyDivider()
        ContainedButton(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(48.dp),
            enabled = enabled,
            label = label,
            onClick = onClick
        )
    }
}