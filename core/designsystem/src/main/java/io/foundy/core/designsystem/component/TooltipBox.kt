package io.foundy.core.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.PlainTooltipState
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TooltipBoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.foundy.core.designsystem.theme.CamstudyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamstudyTooltipBox(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    tooltipState: PlainTooltipState = remember { PlainTooltipState() },
    content: @Composable TooltipBoxScope.() -> Unit
) {
    PlainTooltipBox(
        tooltip = {
            ProvideTextStyle(
                value = CamstudyTheme.typography.titleMedium
            ) {
                tooltip()
            }
        },
        modifier = modifier,
        tooltipState = tooltipState,
        content = content
    )
}
