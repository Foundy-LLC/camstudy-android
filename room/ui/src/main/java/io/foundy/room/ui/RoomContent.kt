package io.foundy.room.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.constant.RoomConstants
import io.foundy.room.domain.PomodoroTimerProperty
import io.foundy.room.domain.PomodoroTimerState
import io.foundy.room.ui.media.FakeMediaManager
import io.foundy.room.ui.media.LocalMediaManager
import io.foundy.room.ui.media.MediaManager
import io.foundy.room.ui.peer.PeerUiState
import io.foundy.room.ui.screen.StudyRoomScreen
import io.foundy.room.ui.screen.WaitingRoomScreen
import io.foundy.room.ui.viewmodel.RoomSideEffect
import io.foundy.room.ui.viewmodel.RoomUiState
import io.foundy.room.ui.viewmodel.RoomViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun RoomScreen(
    modifier: Modifier = Modifier,
    roomOverview: RoomOverview,
    popBackStack: () -> Unit,
    viewModel: RoomViewModel,
    mediaManager: MediaManager,
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showRecheckDialog by remember { mutableStateOf(false) }
    var showDisconnectedDialog by remember { mutableStateOf(false) }
    val handleBackClick = {
        if (uiState is RoomUiState.StudyRoom) {
            showRecheckDialog = !showRecheckDialog
        } else {
            popBackStack()
        }
    }

    viewModel.collectSideEffect {
        when (it) {
            is RoomSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.content ?: context.getString(it.defaultContentRes, it.stringResArgs)
                )
            }
            RoomSideEffect.Disconnected -> {
                showDisconnectedDialog = true
            }
        }
    }

    LaunchedEffect(roomOverview.id) {
        viewModel.connect(roomOverview.id)
    }

    LaunchedEffect(Unit) {
        mediaManager.onSessionScreenReady()
    }

    BackHandler {
        handleBackClick()
    }

    if (showRecheckDialog) {
        CamstudyDialog(
            content = stringResource(R.string.recheck_dialog_content),
            confirmText = stringResource(R.string.exit),
            onCancel = { showRecheckDialog = false },
            onDismissRequest = { showRecheckDialog = false },
            onConfirm = popBackStack
        )
    }

    if (showDisconnectedDialog) {
        CamstudyDialog(
            content = stringResource(R.string.disconnected_room_socket),
            confirmText = stringResource(R.string.exit),
            onDismissRequest = {},
            onConfirm = popBackStack
        )
    }

    CompositionLocalProvider(LocalMediaManager provides mediaManager) {
        RoomContent(
            modifier = modifier,
            roomOverview = roomOverview,
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackClick = handleBackClick,
            popBackStack = popBackStack
        )
    }
}

@Composable
private fun RoomContent(
    modifier: Modifier = Modifier,
    roomOverview: RoomOverview,
    uiState: RoomUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    popBackStack: () -> Unit,
) {
    when (uiState) {
        is RoomUiState.WaitingRoom -> WaitingRoomScreen(
            modifier = modifier,
            roomOverview = roomOverview,
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick
        )
        is RoomUiState.StudyRoom -> StudyRoomScreen(
            modifier = modifier,
            uiState = uiState,
            onDismissKickedDialog = popBackStack,
            onBackClick = onBackClick,
            snackbarHostState = snackbarHostState,
            title = roomOverview.title
        )
    }
}

@Preview
@Composable
private fun RoomContentPreview() {
    CompositionLocalProvider(LocalMediaManager provides FakeMediaManager()) {
        CamstudyTheme {
            RoomContent(
                roomOverview = RoomOverview(
                    id = "id",
                    title = "방제목",
                    masterId = "id",
                    hasPassword = true,
                    thumbnail = null,
                    joinCount = 0,
                    joinedUsers = emptyList(),
                    maxCount = RoomConstants.MaxPeerCount,
                    tags = listOf("tag1")
                ),
                uiState = RoomUiState.StudyRoom(
                    peerStates = listOf(
                        PeerUiState(
                            uid = "id",
                            name = "홍길동",
                            enabledMicrophone = false,
                            enabledHeadset = false,
                            audioTrack = null,
                            videoTrack = null,
                            isMe = false
                        )
                    ),
                    isCurrentUserMaster = false,
                    blacklist = emptyList(),
                    onKickUserClick = {},
                    onSendChatClick = {},
                    onStartPomodoroClick = {},
                    onBlockUserClick = {},
                    onSavePomodoroTimerClick = {},
                    onUnblockUserClick = {},
                    pomodoroTimerEventDate = null,
                    pomodoroTimer = PomodoroTimerProperty(
                        timerLengthMinutes = 25,
                        shortBreakMinutes = 5,
                        longBreakMinutes = 15,
                        longBreakInterval = 4
                    ),
                    pomodoroTimerState = PomodoroTimerState.STOPPED,
                    onChatMessageInputChange = {}
                ),
                snackbarHostState = SnackbarHostState(),
                onBackClick = {},
                popBackStack = {}
            )
        }
    }
}
