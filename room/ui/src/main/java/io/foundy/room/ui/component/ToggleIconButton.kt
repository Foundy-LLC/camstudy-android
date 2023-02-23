package io.foundy.room.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcon

@Composable
fun ToggleIconButton(
    enabled: Boolean,
    enabledIcon: CamstudyIcon,
    disabledIcon: CamstudyIcon,
    onClick: (enabled: Boolean) -> Unit
) {
    val (icon, backgroundColor) = when (enabled) {
        true -> Pair(enabledIcon, MaterialTheme.colorScheme.onBackground)
        false -> Pair(disabledIcon, MaterialTheme.colorScheme.error)
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
            tint = MaterialTheme.colorScheme.onError,
            icon = icon,
            contentDescription = null
        )
    }
}
