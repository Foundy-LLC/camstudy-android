package io.foundy.room.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcon

private val PeerContentIconColor = Color(0xFF333333)

@Composable
fun PeerContentIcon(
    modifier: Modifier = Modifier,
    icon: CamstudyIcon
) {
    CamstudyIcon(
        modifier = modifier
            .padding(16.dp)
            .size(120.dp),
        icon = icon,
        contentDescription = null,
        tint = PeerContentIconColor
    )
}
