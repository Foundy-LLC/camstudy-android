package io.foundy.room.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
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
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.room.domain.ChatMessage
import io.foundy.room.domain.PeerOverview
import io.foundy.room.domain.PomodoroTimerState
import io.foundy.room.domain.WebRtcServerTimeZone
import io.foundy.room.ui.R
import io.foundy.room.ui.component.FloatingVideoRenderer
import io.foundy.room.ui.component.MediaController
import io.foundy.room.ui.component.PomodoroTimerEditBottomSheet
import io.foundy.room.ui.component.VideoRenderer
import io.foundy.room.ui.component.rememberPomodoroTimerEditBottomSheetState
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
    onDismissKickedDialog: () -> Unit,
    startChatActivity: (List<ChatMessage>) -> Unit,
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
    var showBlacklistBottomSheet by remember { mutableStateOf(false) }
    var showPomodoroTimerEditBottomSheet by remember { mutableStateOf(false) }
    var parentBounds: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

    if (showBlacklistBottomSheet) {
        BlacklistBottomSheet(
            blacklist = uiState.blacklist,
            onDeleteClick = { peer ->
                uiState.onUnblockUserClick(peer.id)
                showBlacklistBottomSheet = false
            },
            onDismissRequest = { showBlacklistBottomSheet = false }
        )
    }

    if (uiState.isCurrentUserKicked) {
        // TODO: 아래 코드 다른 composable 함수로 분리하기
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
        // TODO: 아래 코드 다른 composable 함수로 분리하기
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
        // TODO: 아래 코드 다른 composable 함수로 분리하기
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

    if (showPomodoroTimerEditBottomSheet) {
        PomodoroTimerEditBottomSheet(
            state = rememberPomodoroTimerEditBottomSheetState(uiState.pomodoroTimer),
            onSaveClick = { property ->
                uiState.onSavePomodoroTimerClick(property)
                showPomodoroTimerEditBottomSheet = false
            },
            onDismiss = { showPomodoroTimerEditBottomSheet = false }
        )
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
            IconButton(onClick = mediaManager::switchCamera, enabled = enabledLocalVideo) {
                CamstudyIcon(
                    icon = CamstudyIcons.SwitchVideo,
                    contentDescription = stringResource(R.string.switch_video)
                )
            }
            if (uiState.isCurrentUserMaster) {
                IconButton(onClick = { showBlacklistBottomSheet = true }) {
                    CamstudyIcon(
                        icon = CamstudyIcons.Person,
                        contentDescription = stringResource(R.string.blacklist)
                    )
                }
                IconButton(onClick = { showPomodoroTimerEditBottomSheet = true }) {
                    CamstudyIcon(
                        icon = CamstudyIcons.Timer,
                        contentDescription = stringResource(R.string.edit_pomodoro_timer)
                    )
                }
            }
            IconButton(
                onClick = {
                    startChatActivity(uiState.chatMessages)
                }
            ) {
                CamstudyIcon(
                    icon = CamstudyIcons.Chat,
                    contentDescription = stringResource(R.string.show_chat_messages)
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
        color = CamstudyTheme.colorScheme.text01
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
                    tint = CamstudyTheme.colorScheme.systemBackground,
                    contentDescription = null
                )
            }
            if (!peerState.enabledHeadset) {
                CamstudyIcon(
                    icon = CamstudyIcons.HeadsetOff,
                    tint = CamstudyTheme.colorScheme.systemBackground,
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
                    tint = CamstudyTheme.colorScheme.systemBackground,
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
        PomodoroTimerState.STOPPED -> CamstudyTheme.colorScheme.text01
        PomodoroTimerState.STARTED -> CamstudyTheme.colorScheme.error
        PomodoroTimerState.SHORT_BREAK -> CamstudyTheme.colorScheme.primary
        PomodoroTimerState.LONG_BREAK -> CamstudyTheme.colorScheme.secondary
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

@Composable
private fun BlacklistBottomSheet(
    blacklist: List<PeerOverview>,
    onDeleteClick: (PeerOverview) -> Unit,
    onDismissRequest: () -> Unit,
) {
    BottomSheetDialog(
        onDismissRequest = onDismissRequest
    ) {
        Column {
            Text(stringResource(id = R.string.blacklist))
            LazyColumn {
                items(items = blacklist) { peer ->
                    BlacklistItem(
                        peerOverview = peer,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Composable
private fun BlacklistItem(
    peerOverview: PeerOverview,
    onDeleteClick: (PeerOverview) -> Unit
) {
    Row {
        Text(
            text = peerOverview.name,
        )
        IconButton(onClick = { onDeleteClick(peerOverview) }) {
            CamstudyIcon(icon = CamstudyIcons.Delete, contentDescription = null)
        }
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
            color = CamstudyTheme.colorScheme.text01
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
