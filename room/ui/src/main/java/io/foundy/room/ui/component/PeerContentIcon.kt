package io.foundy.room.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun PeerContentIcon(
    modifier: Modifier = Modifier,
    icon: CamstudyIcon
) {
    CamstudyIcon(
        modifier = modifier.size(120.dp),
        icon = icon,
        contentDescription = null,
        tint = CamstudyTheme.colorScheme.systemUi08
    )
}
