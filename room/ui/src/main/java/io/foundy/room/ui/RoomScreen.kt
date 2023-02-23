package io.foundy.room.ui

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.room.data.model.WaitingRoomData
import io.foundy.room.ui.component.ToggleIconButton
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
            is RoomUiState.WaitingRoom -> WaitingRoomScreen(
                roomTitle = id,
                data = uiState.data
            )
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
    roomTitle: String,
    data: WaitingRoomData,
) {
    val mediaManager = LocalMediaManager.current
    val localVideoTrack = mediaManager.localVideoTrackFlow.collectAsState(initial = null).value

    BoxWithConstraints(
        Modifier.fillMaxSize()
    ) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = roomTitle)

            Card(
                modifier = Modifier.size(
                    width = maxWidth / 2.4f,
                    height = maxHeight / 3
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                if (localVideoTrack != null) {
                    VideoRenderer(
                        modifier = Modifier.fillMaxWidth(),
                        eglBaseContext = mediaManager.eglBaseContext,
                        videoTrack = localVideoTrack
                    )
                }
            }
            ToggleIconButton(
                enabled = mediaManager.enabledLocalVideo,
                enabledIcon = CamstudyIcons.VideoCam,
                disabledIcon = CamstudyIcons.VideoCamOff,
                onClick = mediaManager::toggleLocalVideo
            )

            Text(text = data.toString())
            Button(onClick = { /*TODO*/ }) {
                Text(text = "입장")
            }
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
