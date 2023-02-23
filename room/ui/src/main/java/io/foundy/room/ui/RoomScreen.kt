package io.foundy.room.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.room.data.model.WaitingRoomData
import io.foundy.room.ui.component.VideoRenderer
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
            RoomUiState.Connecting -> ConnectingScreen()
            is RoomUiState.FailedToConnect -> FailedToConnectScreen()
            is RoomUiState.WaitingRoom -> WaitingRoomScreen(uiState.data)
        }
    }
}

@Composable
fun ConnectingScreen() {
    Text(text = "Loading...")
}

@Composable
fun FailedToConnectScreen() {
    Text(text = "서버 연결 실패!")
}

@Composable
fun WaitingRoomScreen(
    data: WaitingRoomData,
) {
    val mediaManager = LocalMediaManager.current
    val localVideoTrack = mediaManager.localVideoTrackFlow.collectAsState(initial = null).value

    Text(text = data.toString())
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (localVideoTrack != null) {
            VideoRenderer(
                eglBaseContext = mediaManager.eglBaseContext,
                videoTrack = localVideoTrack
            )
        }
    }
}

@Composable
fun StudyRoomScreen() {
    val mediaManager = LocalMediaManager.current
    val localVideoTrack = mediaManager.localVideoTrackFlow.collectAsState(initial = null).value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (localVideoTrack != null) {
            VideoRenderer(
                eglBaseContext = mediaManager.eglBaseContext,
                videoTrack = localVideoTrack
            )
        }
    }
}
