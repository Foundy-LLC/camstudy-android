package io.foundy.room.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun ToggleIconButton(
    enabled: Boolean,
    enabledIcon: CamstudyIcon,
    disabledIcon: CamstudyIcon,
    onClick: (enabled: Boolean) -> Unit
) {
    val (icon, backgroundColor) = when (enabled) {
        true -> Pair(enabledIcon, CamstudyTheme.colorScheme.text01)
        false -> Pair(disabledIcon, CamstudyTheme.colorScheme.error)
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick(!enabled) },
    ) {
        CamstudyIcon(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Center),
            tint = CamstudyTheme.colorScheme.text01,
            icon = icon,
            contentDescription = null
        )
    }
}
