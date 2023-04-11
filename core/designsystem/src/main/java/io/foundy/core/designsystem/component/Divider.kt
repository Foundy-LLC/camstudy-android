package io.foundy.core.designsystem.component

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun CamstudyDivider(modifier: Modifier = Modifier) {
    Divider(modifier = modifier, color = CamstudyTheme.colorScheme.systemUi03, thickness = 0.5.dp)
}