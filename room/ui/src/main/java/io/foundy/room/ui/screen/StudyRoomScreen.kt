package io.foundy.room.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import io.foundy.room.ui.component.FloatingVideoRenderer
import io.foundy.room.ui.media.LocalMediaManager

@Composable
fun StudyRoomScreen() {
    val mediaManager = LocalMediaManager.current
    val localVideoTrack = mediaManager.localVideoTrackFlow.collectAsState(initial = null).value
    var parentBounds: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { parentBounds = it }
    ) {
        if (localVideoTrack != null) {
            FloatingVideoRenderer(
                eglBaseContext = mediaManager.eglBaseContext,
                videoTrack = localVideoTrack,
                parentBounds = parentBounds
            )
        }
    }
}
