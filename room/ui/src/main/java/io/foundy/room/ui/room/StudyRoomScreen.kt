package io.foundy.room.ui.room

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyBottomSheetDialog
import io.foundy.core.designsystem.component.CamstudyCheckbox
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextButton
import io.foundy.core.designsystem.component.CamstudyTooltipBox
import io.foundy.core.designsystem.component.CamstudyTopAppBar
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

@Composable
fun StudyRoomScreen(
    modifier: Modifier,
    uiState: RoomUiState.StudyRoom,
    title: String,
    snackbarHostState: SnackbarHostState,
    onDismissKickedDialog: () -> Unit,
    onBackClick: () -> Unit
) {
    var shouldExpandChatDivide by remember { mutableStateOf(false) }

    StudyRoomContent(
        modifier = modifier,
        uiState = uiState,
        title = title,
        snackbarHostState = snackbarHostState,
        onDismissKickedDialog = onDismissKickedDialog,
        onBackClick = onBackClick,
        shouldExpandChatDivide = shouldExpandChatDivide,
        onChatExpandClick = { shouldExpandChatDivide = true },
        onChatCollapseClick = { shouldExpandChatDivide = false },
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StudyRoomContent(
    modifier: Modifier = Modifier,
    uiState: RoomUiState.StudyRoom,
    title: String,
    shouldExpandChatDivide: Boolean,
    snackbarHostState: SnackbarHostState,
    onChatExpandClick: () -> Unit,
    onChatCollapseClick: () -> Unit,
    onDismissKickedDialog: () -> Unit,
    onBackClick: () -> Unit,
    userOptionBottomSheetState: UserOptionBottomSheetState = remember {
        UserOptionBottomSheetState()
    },
    kickUserRecheckDialogState: KickUserRecheckDialogState = remember {
        KickUserRecheckDialogState()
    }
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

    KickUserRecheckDialog(
        state = kickUserRecheckDialogState,
        onBlockUserClick = uiState.onBlockUserClick,
        onKickUserClick = uiState.onKickUserClick
    )

    UserOptionBottomSheet(
        state = userOptionBottomSheetState,
        onKickClick = {
            kickUserRecheckDialogState.show(
                userId = it.id,
                userName = it.name
            )
        }
    )

    if (showPomodoroTimerEditBottomSheet) {
        PomodoroTimerEditBottomSheet(
            state = rememberPomodoroTimerEditBottomSheetState(uiState.pomodoroTimer),
            onSaveClick = { property ->
                uiState.onSavePomodoroTimerClick(property)
                showPomodoroTimerEditBottomSheet = false
            },
            onDismiss = { showPomodoroTimerEditBottomSheet = false },
            isTimerRunning = uiState.pomodoroTimerState != PomodoroTimerState.STOPPED
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CamstudyTopAppBar(
                onBackClick = onBackClick,
                title = {
                    Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                trailing = {
                    if (uiState.isCurrentUserMaster) {
                        CamstudyTooltipBox(
                            tooltip = {
                                CamstudyText(text = stringResource(R.string.blacklist))
                            }
                        ) {
                            IconButton(
                                modifier = Modifier.tooltipAnchor(),
                                onClick = { showBlacklistBottomSheet = true }
                            ) {
                                CamstudyIcon(
                                    icon = CamstudyIcons.NoAccounts,
                                    contentDescription = stringResource(R.string.blacklist),
                                    tint = CamstudyTheme.colorScheme.systemUi09
                                )
                            }
                        }
                        CamstudyTooltipBox(
                            tooltip = {
                                CamstudyText(text = stringResource(R.string.edit_pomodoro_timer))
                            }
                        ) {
                            IconButton(
                                modifier = Modifier.tooltipAnchor(),
                                onClick = { showPomodoroTimerEditBottomSheet = true }
                            ) {
                                CamstudyIcon(
                                    icon = CamstudyIcons.Timer,
                                    contentDescription = stringResource(
                                        id = R.string.edit_pomodoro_timer
                                    ),
                                    tint = CamstudyTheme.colorScheme.systemUi09
                                )
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .clickable(
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
                    onMoreButtonClick = userOptionBottomSheetState::show
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
    CamstudyBottomSheetDialog(
        onDismissRequest = onDismissRequest
    ) {
        Column {
            CamstudyText(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                text = stringResource(id = R.string.blacklist),
                style = CamstudyTheme.typography.titleLarge.copy(
                    color = CamstudyTheme.colorScheme.systemUi09,
                    fontWeight = FontWeight.SemiBold
                )
            )
            if (blacklist.isEmpty()) {
                CamstudyText(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 20.dp),
                    text = stringResource(R.string.empty_blacklist),
                    style = CamstudyTheme.typography.titleMedium.copy(
                        color = CamstudyTheme.colorScheme.systemUi05
                    )
                )
            } else {
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
}

@Composable
private fun BlacklistItem(
    peerOverview: PeerOverview,
    onDeleteClick: (PeerOverview) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 8.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CamstudyText(
            modifier = Modifier.weight(1f),
            text = peerOverview.name,
            style = CamstudyTheme.typography.titleMedium.copy(
                color = CamstudyTheme.colorScheme.systemUi07,
                fontWeight = FontWeight.Medium
            )
        )
        CamstudyTextButton(
            label = stringResource(R.string.remove_from_blacklist),
            onClick = { onDeleteClick(peerOverview) }
        )
    }
}

@Immutable
data class UserState(
    val id: String,
    val name: String,
)

@Stable
class UserOptionBottomSheetState {

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

@Composable
fun UserOptionBottomSheet(
    state: UserOptionBottomSheetState,
    onKickClick: (UserState) -> Unit
) {
    val selectedUser = state.selectedUser

    if (selectedUser != null) {
        CamstudyBottomSheetDialog(onDismissRequest = state::hide) {
            Column {
                UserOptionItem(
                    leadingIcon = CamstudyIcons.PersonOff,
                    label = stringResource(R.string.kick_user),
                    onClick = {
                        onKickClick(selectedUser)
                        state.hide()
                    }
                )
            }
        }
    }
}

@Composable
fun UserOptionItem(
    leadingIcon: CamstudyIcon,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CamstudyIcon(
            icon = leadingIcon,
            contentDescription = null,
            tint = CamstudyTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(16.dp))
        CamstudyText(
            text = label,
            style = CamstudyTheme.typography.titleMedium.copy(
                color = CamstudyTheme.colorScheme.systemUi08,
                fontWeight = FontWeight.SemiBold
            )
        )
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
fun KickUserRecheckDialog(
    state: KickUserRecheckDialogState,
    onBlockUserClick: (String) -> Unit,
    onKickUserClick: (String) -> Unit

) {
    var wantToBlock by remember { mutableStateOf(false) }
    val userToKick = state.user

    if (userToKick != null) {
        CamstudyDialog(
            title = stringResource(id = R.string.are_you_sure_want_to_kick, userToKick.name),
            content = {
                Row(
                    modifier = Modifier.clickable { wantToBlock = !wantToBlock },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CamstudyCheckbox(
                        checked = wantToBlock,
                        onCheckedChange = { wantToBlock = !wantToBlock }
                    )
                    Text(text = stringResource(R.string.block_user))
                }
            },
            confirmText = stringResource(R.string.kick),
            onConfirm = {
                if (wantToBlock) {
                    onBlockUserClick(userToKick.id)
                } else {
                    onKickUserClick(userToKick.id)
                }
                state.hide()
            },
            onCancel = state::hide,
            onDismissRequest = state::hide,
        )
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
                snackbarHostState = SnackbarHostState(),
                title = "방 제목",
                onDismissKickedDialog = {},
                onBackClick = {}
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
                snackbarHostState = SnackbarHostState(),
                title = "방 제목",
                onDismissKickedDialog = {},
                onBackClick = {}
            )
        }
    }
}

@Preview
@Composable
fun BlacklistBottomSheetPreview() {
    CamstudyTheme {
        BlacklistBottomSheet(
            blacklist = listOf(PeerOverview(id = "id", name = "홍길동")),
            onDismissRequest = {},
            onDeleteClick = {}
        )
    }
}
