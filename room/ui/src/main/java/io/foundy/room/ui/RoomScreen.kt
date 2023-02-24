package io.foundy.room.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun RoomRoute(
    id: String,
    navigator: DestinationsNavigator,
    mediaManager: MediaManager = rememberMediaManager(),
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value

    requestPermissions(
        LocalContext.current as Activity,
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
        0
    )

    LaunchedEffect(id) {
        viewModel.connect(id)
    }

    LaunchedEffect(Unit) {
        mediaManager.onSessionScreenReady()
    }

    BackHandler {
        // TODO: 사용자에게 한 번 더 확인하기
        // TODO: 소켓 연결 끊기
        mediaManager.disconnect()
        navigator.popBackStack()
    }

    CompositionLocalProvider(LocalMediaManager provides mediaManager) {
        when (uiState) {
            is RoomUiState.WaitingRoom -> WaitingRoomScreen(
                roomTitle = id,
                uiState = uiState
            )
        }
    }
}

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
