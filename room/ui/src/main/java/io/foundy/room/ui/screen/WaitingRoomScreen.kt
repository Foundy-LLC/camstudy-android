package io.foundy.room.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.BottomContainedButton
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.component.SelectableTile
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.UserOverview
import io.foundy.core.model.constant.RoomConstants
import io.foundy.core.ui.RoomTile
import io.foundy.room.data.model.RoomJoiner
import io.foundy.room.data.model.WaitingRoomData
import io.foundy.room.ui.R
import io.foundy.room.ui.component.PeerContentIcon
import io.foundy.room.ui.component.VideoRenderer
import io.foundy.room.ui.mapper.toUserOverviewWithoutIntroduce
import io.foundy.room.ui.media.FakeMediaManager
import io.foundy.room.ui.media.LocalMediaManager
import io.foundy.room.ui.viewmodel.RoomUiState

@Immutable
private data class Action(
    val title: String,
    val subtitle: String,
    val checked: Boolean,
    val onCheckChange: (Boolean) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingRoomScreen(
    roomOverview: RoomOverview,
    uiState: RoomUiState.WaitingRoom,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit
) {
    if (uiState is RoomUiState.WaitingRoom.NotExists) {
        NotExistsContent()
        return
    }
    val mediaManager = LocalMediaManager.current
    val enabledLocalVideo = mediaManager.enabledLocalVideo
    val enabledLocalAudio = mediaManager.enabledLocalAudio
    val localVideoTrack = mediaManager.localVideoTrackFlow.collectAsState(initial = null).value
    val connectedWaitingRoomUiState = uiState as? RoomUiState.WaitingRoom.Connected

    val actions: List<Action> = listOf(
        Action(
            title = stringResource(R.string.cam_title),
            subtitle = stringResource(R.string.cam_subtitle),
            checked = enabledLocalVideo,
            onCheckChange = mediaManager::toggleVideo
        ),
        Action(
            title = stringResource(R.string.mic_title),
            subtitle = stringResource(R.string.mic_subtitle),
            checked = enabledLocalAudio,
            onCheckChange = mediaManager::toggleMicrophone
        )
    )
    val onJoinClick: () -> Unit = {
        if (uiState is RoomUiState.WaitingRoom.Connected) {
            uiState.onJoinClick(
                localVideoTrack,
                mediaManager.localAudioTrack,
                uiState.passwordInput
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CamstudyTopAppBar(
                onBackClick = onBackClick,
                title = {
                    CamstudyText(
                        text = stringResource(R.string.join_study_room),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(color = CamstudyTheme.colorScheme.systemBackground),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(CamstudyTheme.colorScheme.systemUi09),
                ) {
                    Box(Modifier.align(Alignment.Center)) {
                        if (localVideoTrack != null && enabledLocalVideo) {
                            VideoRenderer(
                                modifier = Modifier.size(200.dp),
                                eglBaseContext = mediaManager.eglBaseContext,
                                videoTrack = localVideoTrack
                            )
                        } else {
                            PeerContentIcon(icon = CamstudyIcons.MaterialVideoCamOff)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                RoomTile(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    room = if (connectedWaitingRoomUiState != null) {
                        roomOverview.copy(
                            joinedUsers = connectedWaitingRoomUiState.data.joinerList.map {
                                it.toUserOverviewWithoutIntroduce()
                            }
                        )
                    } else {
                        roomOverview
                    }
                )
                if (roomOverview.hasPassword) {
                    Spacer(modifier = Modifier.height(20.dp))
                    CamstudyTextField(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        enabled = connectedWaitingRoomUiState != null,
                        value = connectedWaitingRoomUiState?.passwordInput ?: "",
                        onValueChange = connectedWaitingRoomUiState?.onPasswordChange ?: {},
                        placeholder = stringResource(R.string.input_password_of_study_room),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Go,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = { onJoinClick() }
                        )
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                CamstudyDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = CamstudyTheme.colorScheme.systemUi02
                )
                Spacer(modifier = Modifier.height(8.dp))
                for (action in actions) {
                    SelectableTile(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        title = action.title,
                        subtitle = action.subtitle,
                        checked = action.checked,
                        onCheckedChange = action.onCheckChange
                    )
                }
            }
            BottomContainedButton(
                enabled = uiState.isEnabledJoinButton(),
                label = uiState.getCannotJoinMessage()
                    ?: stringResource(id = uiState.joinButtonTextRes),
                onClick = onJoinClick
            )
        }
    }
}

@Composable
private fun NotExistsContent() {
    Text(text = stringResource(R.string.not_exists_study_room))
}

@Preview
@Composable
private fun WaitingRoomScreenPreview() {
    CompositionLocalProvider(LocalMediaManager provides FakeMediaManager()) {
        CamstudyTheme {
            WaitingRoomScreen(
                roomOverview = RoomOverview(
                    id = "id",
                    title = "방제목",
                    masterId = "id",
                    hasPassword = true,
                    thumbnail = null,
                    joinCount = 1,
                    joinedUsers = listOf(
                        UserOverview(
                            id = "id123",
                            name = "홍김박",
                            profileImage = null,
                            introduce = null
                        )
                    ),
                    maxCount = RoomConstants.MaxPeerCount,
                    tags = listOf("tag1")
                ),
                uiState = RoomUiState.WaitingRoom.Connected(
                    currentUserId = "id",
                    data = WaitingRoomData(
                        joinerList = listOf(
                            RoomJoiner(
                                id = "id3",
                                name = "홍길동",
                                profileImage = null
                            )
                        ),
                        capacity = RoomConstants.MaxPeerCount,
                        masterId = "123",
                        blacklist = listOf(),
                        hasPassword = true
                    ),
                    onPasswordChange = {},
                    onJoinClick = { _, _, _ -> }
                ),
                snackbarHostState = SnackbarHostState(),
                onBackClick = {}
            )
        }
    }
}
