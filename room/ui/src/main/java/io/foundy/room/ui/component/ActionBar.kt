package io.foundy.room.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun ActionBar(
    enabledLocalVideo: Boolean,
    enabledLocalAudio: Boolean,
    enabledLocalHeadset: Boolean,
    onToggleVideo: (Boolean) -> Unit,
    onToggleAudio: (Boolean) -> Unit,
    onToggleHeadset: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CamstudyTheme.colorScheme.systemUi09
    ) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                ToggleIconButton(
                    enabled = enabledLocalVideo,
                    enabledIcon = CamstudyIcons.VideoCam,
                    disabledIcon = CamstudyIcons.VideoCamOff,
                    onClick = onToggleVideo
                )
                ToggleIconButton(
                    enabled = enabledLocalHeadset,
                    enabledIcon = CamstudyIcons.Headset,
                    disabledIcon = CamstudyIcons.HeadsetOff,
                    onClick = onToggleHeadset
                )
                ToggleIconButton(
                    enabled = enabledLocalAudio,
                    enabledIcon = CamstudyIcons.Mic,
                    disabledIcon = CamstudyIcons.MicOff,
                    onClick = onToggleAudio
                )
            }
        }
    }
}

@Preview(widthDp = 200)
@Composable
private fun ActionBarPreview() {
    CamstudyTheme {
        ActionBar(
            enabledLocalVideo = true,
            enabledLocalAudio = true,
            enabledLocalHeadset = true,
            onToggleVideo = {},
            onToggleAudio = {},
            onToggleHeadset = {}
        )
    }
}
