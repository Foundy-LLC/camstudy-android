package io.foundy.room.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcon

@Composable
fun ToggleIconButton(
    enabled: Boolean,
    enabledIcon: CamstudyIcon,
    disabledIcon: CamstudyIcon,
    onClick: (enabled: Boolean) -> Unit
) {
    IconButton(
        modifier = Modifier,
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
