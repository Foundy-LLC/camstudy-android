package io.foundy.room.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.domain.PomodoroTimerState
import com.example.domain.WebRtcServerTimeZone
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.room.ui.R
import io.foundy.room.ui.component.FloatingVideoRenderer
import io.foundy.room.ui.component.MediaController
import io.foundy.room.ui.component.VideoRenderer
import io.foundy.room.ui.media.LocalMediaManager
import io.foundy.room.ui.peer.PeerUiState
import io.foundy.room.ui.viewmodel.RoomUiState
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Composable
fun StudyRoomScreen(
    uiState: RoomUiState.StudyRoom,
    userBottomSheetState: UserBottomSheetState = remember { UserBottomSheetState() },
    kickUserRecheckDialogState: KickUserRecheckDialogState = remember {
        KickUserRecheckDialogState()
    },
    onDismissKickedDialog: () -> Unit
) {
    if (uiState.isPipMode) {
        StudyRoomContentInPip(uiState = uiState)
        return
    }

    val mediaManager = LocalMediaManager.current
    val enabledLocalVideo = mediaManager.enabledLocalVideo
    val enabledLocalAudio = mediaManager.enabledLocalAudio
    val enabledLocalHeadset = mediaManager.enabledLocalHeadset
    val localVideoTrack = mediaManager.localVideoTrackFlow.collectAsState(initial = null).value
    var parentBounds: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

    if (uiState.isCurrentUserKicked) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.master_kicked_you)) },
            confirmButton = {
                TextButton(onClick = onDismissKickedDialog) {
                    Text(text = stringResource(R.string.back_to_home))
                }
            },
            onDismissRequest = onDismissKickedDialog,
        )
    }

    val userToKick = kickUserRecheckDialogState.user
    if (userToKick != null) {
        var checkedBlock by remember { mutableStateOf(false) }
        AlertDialog(
            title = {
                Text(
                    text = stringResource(id = R.string.are_you_sure_want_to_kick, userToKick.name)
                )
            },
            text = {
                Row(
                    modifier = Modifier.clickable { checkedBlock = !checkedBlock }
                ) {
                    Checkbox(
                        checked = checkedBlock,
                        onCheckedChange = { checkedBlock = !checkedBlock }
                    )
                    Text(text = stringResource(R.string.block_user))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (checkedBlock) {
                            uiState.onBlockUserClick(userToKick.id)
                        } else {
                            uiState.onKickUserClick(userToKick.id)
                        }
                        kickUserRecheckDialogState.hide()
                    }
                ) {
                    Text(text = stringResource(R.string.kick))
                }
            },
            dismissButton = {
                TextButton(onClick = kickUserRecheckDialogState::hide) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            onDismissRequest = kickUserRecheckDialogState::hide,
        )
    }

    val selectedUser = userBottomSheetState.selectedUser
    if (selectedUser != null) {
        BottomSheetDialog(onDismissRequest = userBottomSheetState::hide) {
            Button(
                onClick = {
                    kickUserRecheckDialogState.show(
                        userId = selectedUser.id,
                        userName = selectedUser.name
                    )
                    userBottomSheetState.hide()
                }
            ) {
                Text(stringResource(R.string.kick_user))
            }
        }
    }

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
        Column {
            PomodoroTimer(
                state = uiState.pomodoroTimerState,
                pomodoroTimerEventDate = uiState.pomodoroTimerEventDate
            )
            if (uiState.pomodoroTimerState == PomodoroTimerState.STOPPED) {
                PomodoroTimerStartButton(
                    onStartClick = uiState.onStartPomodoroClick
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
            ) {
                items(uiState.peerStates, key = { it.uid }) { peerState ->
                    RemotePeer(
                        peerState = peerState,
                        isCurrentUserMaster = uiState.isCurrentUserMaster,
                        onMoreButtonClick = userBottomSheetState::show
                    )
                }
            }
            MediaController(
                enabledLocalVideo = enabledLocalVideo,
                enabledLocalAudio = enabledLocalAudio,
                enabledLocalHeadset = enabledLocalHeadset,
                onToggleVideo = mediaManager::toggleVideo,
                onToggleAudio = mediaManager::toggleMicrophone,
                onToggleHeadset = mediaManager::toggleHeadset,
            )
        }
    }
}

@Composable
private fun RemotePeer(
    peerState: PeerUiState,
    isCurrentUserMaster: Boolean,
    onMoreButtonClick: (userId: String, userName: String) -> Unit
) {
    val mediaManager = LocalMediaManager.current
    val videoSizeModifier = Modifier.size(width = 128.dp, height = 200.dp)

    Surface(
        modifier = videoSizeModifier,
        color = MaterialTheme.colorScheme.onBackground
    ) {
        if (peerState.videoTrack != null) {
            VideoRenderer(
                modifier = videoSizeModifier,
                eglBaseContext = mediaManager.eglBaseContext,
                videoTrack = peerState.videoTrack
            )
        }
        Row {
            if (!peerState.enabledMicrophone) {
                CamstudyIcon(
                    icon = CamstudyIcons.MicOff,
                    tint = MaterialTheme.colorScheme.background,
                    contentDescription = null
                )
            }
            if (!peerState.enabledHeadset) {
                CamstudyIcon(
                    icon = CamstudyIcons.HeadsetOff,
                    tint = MaterialTheme.colorScheme.background,
                    contentDescription = null
                )
            }
        }

        if (isCurrentUserMaster) {
            IconButton(
                onClick = { onMoreButtonClick(peerState.uid, peerState.name) },
            ) {
                CamstudyIcon(
                    icon = CamstudyIcons.MoreVert,
                    tint = MaterialTheme.colorScheme.background,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun PomodoroTimer(
    state: PomodoroTimerState,
    pomodoroTimerEventDate: LocalDateTime?,
) {
    var elapsedTimeText by remember { mutableStateOf(pomodoroTimerEventDate.elapsedTimeText) }

    if (state != PomodoroTimerState.STOPPED) {
        LaunchedEffect(pomodoroTimerEventDate) {
            while (true) {
                delay(1_000)
                elapsedTimeText = pomodoroTimerEventDate.elapsedTimeText
            }
        }
    }
    val color = when (state) {
        PomodoroTimerState.STOPPED -> MaterialTheme.colorScheme.onBackground
        PomodoroTimerState.STARTED -> MaterialTheme.colorScheme.error
        PomodoroTimerState.SHORT_BREAK -> MaterialTheme.colorScheme.primary
        PomodoroTimerState.LONG_BREAK -> MaterialTheme.colorScheme.secondary
    }
    Text(
        text = elapsedTimeText,
        color = color
    )
}

@Composable
fun PomodoroTimerStartButton(
    onStartClick: () -> Unit
) {
    Button(onClick = onStartClick) {
        Text(text = "시작")
    }
}

private val LocalDateTime?.elapsedTimeText: String
    get() {
        if (this == null) {
            return "00:00"
        }
        val currentTime = Clock.System.now().toLocalDateTime(WebRtcServerTimeZone)
        val instantDiff = currentTime.toInstant(WebRtcServerTimeZone) -
            this.toInstant(WebRtcServerTimeZone)
        val diffWholeSeconds = instantDiff.inWholeSeconds
        val minutes = diffWholeSeconds / 60
        val seconds = diffWholeSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

@Immutable
data class UserState(
    val id: String,
    val name: String,
)

@Stable
class UserBottomSheetState {

    var selectedUser by mutableStateOf<UserState?>(null)
        private set

    fun show(userId: String, userName: String) {
        selectedUser = UserState(
            name = userName,
            id = userId
        )
    }

    fun hide() {
        selectedUser = null
    }
}

@Stable
class KickUserRecheckDialogState {

    var user by mutableStateOf<UserState?>(null)
        private set

    fun show(userId: String, userName: String) {
        user = UserState(
            name = userName,
            id = userId
        )
    }

    fun hide() {
        user = null
    }
}

@Composable
fun StudyRoomContentInPip(
    uiState: RoomUiState.StudyRoom
) {
    val mediaManager = LocalMediaManager.current
    val videoSizeModifier = Modifier.size(width = 128.dp, height = 128.dp)
    val localVideoTrack = mediaManager.localVideoTrackFlow.collectAsState(initial = null).value

    Row {
        PomodoroTimer(
            state = uiState.pomodoroTimerState,
            pomodoroTimerEventDate = uiState.pomodoroTimerEventDate
        )
        Surface(
            modifier = videoSizeModifier,
            color = MaterialTheme.colorScheme.onBackground
        ) {
            if (localVideoTrack != null) {
                VideoRenderer(
                    modifier = videoSizeModifier,
                    eglBaseContext = mediaManager.eglBaseContext,
                    videoTrack = localVideoTrack
                )
            }
        }
    }
}
