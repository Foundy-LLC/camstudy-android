package io.foundy.core.designsystem.component

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun CamstudyDivider(
    modifier: Modifier = Modifier,
    color: Color = CamstudyTheme.colorScheme.systemUi03
) {
    Divider(modifier = modifier, color = color, thickness = 0.5.dp)
}
