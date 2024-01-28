package io.foundy.crop.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
internal fun DivideTitle(
    modifier: Modifier = Modifier,
    text: String
) {
    CamstudyText(
        modifier = modifier,
        text = text,
        style = CamstudyTheme.typography.titleMedium.copy(
            color = CamstudyTheme.colorScheme.systemUi07
        )
    )
}
