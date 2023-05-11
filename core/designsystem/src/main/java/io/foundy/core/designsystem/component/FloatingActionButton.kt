package io.foundy.core.designsystem.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

val FloatingActionButtonBottomPadding = 80.dp

@Composable
fun CamstudyExtendedFloatingActionButton(
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
) {
    ExtendedFloatingActionButton(
        text = {
            ProvideTextStyle(
                value = CamstudyTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            ) {
                text()
            }
        },
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        expanded = expanded,
        shape = RoundedCornerShape(100.dp),
        containerColor = CamstudyTheme.colorScheme.systemBackground,
        contentColor = CamstudyTheme.colorScheme.primary
    )
}
