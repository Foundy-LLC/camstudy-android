package io.foundy.room.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.constant.RoomConstants.MaxPeerCount
import io.foundy.room.domain.PeerOverview
import io.foundy.room.domain.PomodoroTimerProperty
import io.foundy.room.domain.PomodoroTimerState
import io.foundy.room.ui.R
import io.foundy.room.ui.component.ActionBar
import io.foundy.room.ui.component.ChatDivide
import io.foundy.room.ui.component.PeerContent
import io.foundy.room.ui.component.PeerContentIcon
import io.foundy.room.ui.component.PomodoroTimerEditBottomSheet
import io.foundy.room.ui.component.VideoRenderer
import io.foundy.room.ui.component.rememberPomodoroTimerEditBottomSheetState
import io.foundy.room.ui.media.FakeMediaManager
import io.foundy.room.ui.media.LocalMediaManager
import io.foundy.room.ui.peer.PeerUiState
import io.foundy.room.ui.viewmodel.RoomUiState

@Composable
fun StudyRoomScreen(
    uiState: RoomUiState.StudyRoom,
    onDismissKickedDialog: () -> Unit,
) {
    var shouldExpandChatDivide by remember { mutableStateOf(false) }

    StudyRoomContent(
        uiState = uiState,
        shouldExpandChatDivide = shouldExpandChatDivide,
        onChatExpandClick = { shouldExpandChatDivide = true },
        onChatCollapseClick = { shouldExpandChatDivide = false },
        onDismissKickedDialog = onDismissKickedDialog
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StudyRoomContent(
    uiState: RoomUiState.StudyRoom,
    shouldExpandChatDivide: Boolean,
    onChatExpandClick: () -> Unit,
    onChatCollapseClick: () -> Unit,
    userBottomSheetState: UserBottomSheetState = remember { UserBottomSheetState() },
    kickUserRecheckDialogState: KickUserRecheckDialogState = remember {
        KickUserRecheckDialogState()
    },
    onDismissKickedDialog: () -> Unit,
) {
    if (uiState.isPipMode) {
        StudyRoomContentInPip()
        return
    }

    val mediaManager = LocalMediaManager.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val enabledLocalVideo = mediaManager.enabledLocalVideo
    val enabledLocalAudio = mediaManager.enabledLocalAudio
    val enabledLocalHeadset = mediaManager.enabledLocalHeadset
    var showBlacklistBottomSheet by remember { mutableStateOf(false) }
    var showPomodoroTimerEditBottomSheet by remember { mutableStateOf(false) }

    val freeChatFocus: () -> Unit = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }

    BackHandler(enabled = shouldExpandChatDivide) {
        onChatCollapseClick()
    }

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

    Column(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = freeChatFocus
        )
    ) {
        if (!shouldExpandChatDivide) {
            PeerGridView(
                modifier = Modifier.weight(1f),
                peerStates = listOf(mediaManager.currentUserState) + uiState.peerStates,
                isCurrentUserMaster = uiState.isCurrentUserMaster,
                onMoreButtonClick = userBottomSheetState::show
            )
        }
        ActionBar(
            timerState = uiState.pomodoroTimerState,
            timerEventDate = uiState.pomodoroTimerEventDate,
            onStartTimerClick = uiState.onStartPomodoroClick,
            enabledLocalVideo = enabledLocalVideo,
            enabledLocalAudio = enabledLocalAudio,
            enabledLocalHeadset = enabledLocalHeadset,
            onToggleVideo = mediaManager::toggleVideo,
            onToggleAudio = mediaManager::toggleMicrophone,
            onToggleHeadset = mediaManager::toggleHeadset,
            onFlipCamera = mediaManager::switchCamera
        )
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
        ChatDivide(
            chatInput = uiState.chatMessageInput,
            onChatInputChange = uiState.onChatMessageInputChange,
            onSendClick = uiState.onSendChatClick,
            messages = uiState.chatMessages,
            expanded = shouldExpandChatDivide,
            onExpandClick = onChatExpandClick,
            onCollapseClick = onChatCollapseClick
        )
    }
}

@Composable
private fun PeerGridView(
    modifier: Modifier = Modifier,
    peerStates: List<PeerUiState>,
    isCurrentUserMaster: Boolean,
    onMoreButtonClick: (id: String, name: String) -> Unit
) {
    Surface(
        modifier = modifier,
        color = CamstudyTheme.colorScheme.systemUi08
    ) {
        BoxWithConstraints {
            val width = maxWidth / 2
            val height = maxHeight / 2
            val sizeModifier = Modifier
                .width(width)
                .height(height)

            LazyVerticalGrid(
                columns = GridCells.Fixed(count = 2),
            ) {
                items(peerStates, key = { it.uid }) { peerState ->
                    PeerContent(
                        modifier = sizeModifier,
                        peerState = peerState,
                        showMoreButton = isCurrentUserMaster && !peerState.isMe,
                        onMoreButtonClick = onMoreButtonClick
                    )
                }
                items(MaxPeerCount - peerStates.size) {
                    Box(
                        modifier = sizeModifier.background(
                            color = CamstudyTheme.colorScheme.systemUi09
                        )
                    ) {
                        PeerContentIcon(
                            modifier = Modifier.align(Alignment.Center),
                            icon = CamstudyIcons.PersonOff
                        )
                    }
                }
            }
        }
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
fun StudyRoomContentInPip() {
    val mediaManager = LocalMediaManager.current
    val videoSizeModifier = Modifier.size(width = 128.dp, height = 128.dp)
    val localVideoTrack = mediaManager.localVideoTrackFlow.collectAsState(initial = null).value

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

@Preview(widthDp = 300, heightDp = 400)
@Composable
private fun PeerGridViewPreview() {
    CompositionLocalProvider(LocalMediaManager provides FakeMediaManager()) {
        CamstudyTheme {
            PeerGridView(
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
                isCurrentUserMaster = true,
                onMoreButtonClick = { _, _ -> }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudyRoomScreenPreview() {
    CompositionLocalProvider(LocalMediaManager provides FakeMediaManager()) {
        CamstudyTheme {
            StudyRoomContent(
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
                    onChatMessageInputChange = {},
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
                shouldExpandChatDivide = false,
                onChatCollapseClick = {},
                onChatExpandClick = {},
                onDismissKickedDialog = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandedChatStudyRoomScreenPreview() {
    CompositionLocalProvider(LocalMediaManager provides FakeMediaManager()) {
        CamstudyTheme {
            StudyRoomContent(
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
                    onChatMessageInputChange = {},
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
                shouldExpandChatDivide = true,
                onChatCollapseClick = {},
                onChatExpandClick = {},
                onDismissKickedDialog = {},
            )
        }
    }
}
