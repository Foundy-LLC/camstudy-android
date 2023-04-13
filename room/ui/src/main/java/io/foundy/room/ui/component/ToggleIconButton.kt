package io.foundy.room.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyTooltipBox
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.theme.CamstudyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToggleIconButton(
    enabled: Boolean,
    tooltipMessage: String,
    enabledIcon: CamstudyIcon,
    disabledIcon: CamstudyIcon,
    onClick: (enabled: Boolean) -> Unit
) {
    CamstudyTooltipBox(
        tooltip = {
            Text(text = tooltipMessage)
        }
    ) {
        IconButton(
            modifier = Modifier.tooltipAnchor(),
            onClick = { onClick(!enabled) }
        ) {
            CamstudyIcon(
                modifier = Modifier.size(32.dp),
                tint = Color.Unspecified,
                icon = if (enabled) enabledIcon else disabledIcon,
                contentDescription = null
            )
        }
    }
}
