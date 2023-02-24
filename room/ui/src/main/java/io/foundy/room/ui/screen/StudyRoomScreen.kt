package io.foundy.room.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.room.ui.component.FloatingVideoRenderer
import io.foundy.room.ui.component.ToggleIconButton
import io.foundy.room.ui.media.LocalMediaManager

@Composable
fun StudyRoomScreen() {
    val mediaManager = LocalMediaManager.current
    val enabledLocalVideo = mediaManager.enabledLocalVideo
    val enabledLocalAudio = mediaManager.enabledLocalAudio
    val localVideoTrack = mediaManager.localVideoTrackFlow.collectAsState(initial = null).value
    var parentBounds: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { parentBounds = it }
    ) {
        if (localVideoTrack != null && enabledLocalVideo) {
            FloatingVideoRenderer(
                modifier = Modifier
                    .size(width = 150.dp, height = 210.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.TopEnd),
                eglBaseContext = mediaManager.eglBaseContext,
                videoTrack = localVideoTrack,
                parentBounds = parentBounds
            )
        }
        Row {
            ToggleIconButton(
                enabled = enabledLocalVideo,
                enabledIcon = CamstudyIcons.VideoCam,
                disabledIcon = CamstudyIcons.VideoCamOff,
                onClick = mediaManager::toggleVideo
            )
            ToggleIconButton(
                enabled = enabledLocalAudio,
                enabledIcon = CamstudyIcons.Mic,
                disabledIcon = CamstudyIcons.MicOff,
                onClick = mediaManager::toggleMicrophone
            )
        }
    }
}
