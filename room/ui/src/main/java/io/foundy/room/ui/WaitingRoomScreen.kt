package io.foundy.room.ui

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.room.ui.component.ToggleIconButton
import io.foundy.room.ui.component.VideoRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingRoomScreen(
    roomTitle: String,
    uiState: RoomUiState.WaitingRoom,
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

            Text(text = uiState.toString())
            uiState.cannotJoinMessage?.let { Text(text = stringResource(id = it)) }

            if (uiState is RoomUiState.WaitingRoom.Connected && uiState.data.hasPassword) {
                TextField(
                    value = uiState.passwordInput,
                    onValueChange = uiState.onPasswordChange,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                )
            }
            Button(
                enabled = uiState.enableJoinButton,
                onClick = { /*TODO*/ },
            ) {
                Text(text = "입장")
            }
        }
    }
}
