package io.foundy.room.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import io.foundy.core.designsystem.icon.CamstudyIcons

@Composable
fun MediaController(
    enabledLocalVideo: Boolean,
    enabledLocalAudio: Boolean,
    enabledLocalHeadset: Boolean,
    onToggleVideo: (Boolean) -> Unit,
    onToggleAudio: (Boolean) -> Unit,
    onToggleHeadset: (Boolean) -> Unit,
) {
    Row {
        ToggleIconButton(
            enabled = enabledLocalVideo,
            enabledIcon = CamstudyIcons.VideoCam,
            disabledIcon = CamstudyIcons.VideoCamOff,
            onClick = onToggleVideo
        )
        ToggleIconButton(
            enabled = enabledLocalAudio,
            enabledIcon = CamstudyIcons.Mic,
            disabledIcon = CamstudyIcons.MicOff,
            onClick = onToggleAudio
        )
        ToggleIconButton(
            enabled = enabledLocalHeadset,
            enabledIcon = CamstudyIcons.Headset,
            disabledIcon = CamstudyIcons.HeadsetOff,
            onClick = onToggleHeadset
        )
    }
}
