package io.foundy.room.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.room.domain.ChatMessage
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
    roomId: String,
    roomTitle: String,
    popBackStack: () -> Unit,
    startChatActivity: (List<ChatMessage>) -> Unit,
    viewModel: RoomViewModel,
    mediaManager: MediaManager,
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    viewModel.collectSideEffect {
        when (it) {
            is RoomSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.content ?: context.getString(it.defaultContentRes, it.stringResArgs)
                )
            }
            is RoomSideEffect.OnChatMessage -> coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    "${it.message.authorName}: ${it.message.content}"
                )
            }
        }
    }

    LaunchedEffect(roomId) {
        viewModel.connect(roomId)
    }

    LaunchedEffect(Unit) {
        mediaManager.onSessionScreenReady()
    }

    BackHandler {
        // TODO: 사용자에게 한 번 더 확인하기
        mediaManager.disconnect()
        popBackStack()
    }

    CompositionLocalProvider(LocalMediaManager provides mediaManager) {
        RoomContent(
            modifier = modifier,
            title = roomTitle,
            roomId = roomId,
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            startChatActivity = startChatActivity,
            popBackStack = popBackStack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomContent(
    modifier: Modifier = Modifier,
    title: String,
    roomId: String,
    uiState: RoomUiState,
    snackbarHostState: SnackbarHostState,
    startChatActivity: (List<ChatMessage>) -> Unit,
    popBackStack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CamstudyTopAppBar(
                onBackClick = popBackStack,
                title = {
                    Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (uiState) {
                is RoomUiState.WaitingRoom -> WaitingRoomScreen(
                    roomTitle = roomId,
                    uiState = uiState
                )
                is RoomUiState.StudyRoom -> StudyRoomScreen(
                    uiState = uiState,
                    onDismissKickedDialog = {
                        popBackStack()
                    },
                    startChatActivity = startChatActivity
                )
            }
        }
    }
}

@Preview
@Composable
private fun RoomContentPreview() {
    CompositionLocalProvider(LocalMediaManager provides FakeMediaManager()) {
        CamstudyTheme {
            RoomContent(
                title = "방제목",
                roomId = "id",
                uiState = RoomUiState.StudyRoom(
                    peerStates = listOf(
                        PeerUiState(
                            uid = "id",
                            name = "홍길동",
                            enabledMicrophone = false,
                            enabledHeadset = false,
                            audioTrack = null,
                            videoTrack = null
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
                    pomodoroTimerState = PomodoroTimerState.STOPPED
                ),
                snackbarHostState = SnackbarHostState(),
                startChatActivity = {},
                popBackStack = {}
            )
        }
    }
}
