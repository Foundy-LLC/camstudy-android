package io.foundy.room.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.model.constant.RoomConstants
import io.foundy.room.data.model.StudyRoomEvent
import io.foundy.room.data.model.WaitingRoomEvent
import io.foundy.room.data.service.RoomService
import io.foundy.room.domain.PeerOverview
import io.foundy.room.domain.PomodoroTimerProperty
import io.foundy.room.domain.WebRtcServerTimeZone
import io.foundy.room.ui.R
import io.foundy.room.ui.media.MediaManager
import io.foundy.room.ui.media.MediaManagerEvent
import io.foundy.room.ui.peer.merge
import io.foundy.room.ui.peer.toInitialUiState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack
import javax.inject.Inject

// TODO: WaitingRoom이랑 StudyRoom 분리하기
@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomService: RoomService,
    private val authRepository: AuthRepository,
    val mediaManager: MediaManager
) : ViewModel(), ContainerHost<RoomUiState, RoomSideEffect> {

    override val container: Container<RoomUiState, RoomSideEffect> =
        container(RoomUiState.WaitingRoom.Loading)

    private var _currentUserId: String? = null
    private val currentUserId: String get() = requireNotNull(_currentUserId)

    init {
        viewModelScope.launch {
            val currentUserId = authRepository.currentUserIdStream.first()
            check(currentUserId != null) {
                "회원 정보를 얻을 수 없습니다. 로그인하지 않고 공부방 접속은 할 수 없습니다."
            }
            _currentUserId = currentUserId
        }
        viewModelScope.launch {
            roomService.eventFlow.collect {
                when (it) {
                    is WaitingRoomEvent -> handleWaitingRoomEvent(it)
                    is StudyRoomEvent -> handleStudyRoomEvent(it)
                }
            }
        }
        viewModelScope.launch {
            mediaManager.mediaEvent.collect { event ->
                when (event) {
                    is MediaManagerEvent.ToggleAudio -> onToggleAudio(event.track)
                    is MediaManagerEvent.ToggleVideo -> onToggleVideo(event.track)
                    is MediaManagerEvent.ToggleHeadset -> onToggleHeadset(event.enabled)
                }
            }
        }
    }

    fun connect(roomId: String) = intent {
        // TODO: 대기실이랑 공부방 Activity 분리되면 check 함수로 수정하기
        if (state !is RoomUiState.WaitingRoom.Loading) {
            return@intent
        }
        try {
            roomService.connect(roomId)
            joinToWaitingRoom(roomId)
        } catch (e: TimeoutCancellationException) {
            reduce {
                RoomUiState.WaitingRoom.FailedToConnect(
                    defaultMessageRes = R.string.timeout_on_connect_server
                )
            }
        } catch (e: Exception) {
            reduce {
                RoomUiState.WaitingRoom.FailedToConnect(
                    message = e.message,
                    defaultMessageRes = R.string.failed_to_connect_server
                )
            }
        }
    }

    private fun joinToWaitingRoom(roomId: String) = intent {
        try {
            val waitingRoomData = roomService.joinToWaitingRoom(roomId)
            if (waitingRoomData == null) {
                reduce { RoomUiState.WaitingRoom.NotExists }
                return@intent
            }
            reduce {
                RoomUiState.WaitingRoom.Connected(
                    data = waitingRoomData,
                    currentUserId = currentUserId,
                    onPasswordChange = { password ->
                        if (password.length <= RoomConstants.MaxPasswordLength) {
                            intent {
                                val uiState = state
                                check(uiState is RoomUiState.WaitingRoom.Connected)
                                reduce { uiState.copy(passwordInput = password) }
                            }
                        }
                    },
                    onJoinClick = ::joinToStudyRoom
                )
            }
        } catch (e: TimeoutCancellationException) {
            reduce {
                RoomUiState.WaitingRoom.FailedToConnect(
                    defaultMessageRes = R.string.failed_to_join_waiting_room
                )
            }
        }
    }

    private fun joinToStudyRoom(
        localVideo: VideoTrack?,
        localAudio: AudioTrack?,
        password: String
    ) = intent {
        val uiState = state
        check(uiState is RoomUiState.WaitingRoom.Connected)
        try {
            reduce { uiState.copy(joining = true) }
            roomService.joinToStudyRoom(
                localVideo = localVideo,
                localAudio = localAudio,
                userId = currentUserId,
                password = password
            ).onSuccess {
                reduce {
                    RoomUiState.StudyRoom(
                        peerStates = it.peerStates.map { peerState ->
                            peerState.toInitialUiState(isMe = currentUserId == peerState.uid)
                        },
                        isCurrentUserMaster = uiState.isCurrentUserMaster,
                        blacklist = uiState.data.blacklist,
                        onKickUserClick = ::kickUser,
                        onBlockUserClick = ::blockUser,
                        onUnblockUserClick = ::unblockUser,
                        onSavePomodoroTimerClick = ::updatePomodoroTimer,
                        pomodoroTimerEventDate = it.timerStartedDateTime,
                        pomodoroTimer = it.timerProperty,
                        pomodoroTimerState = it.timerState,
                        onStartPomodoroClick = ::startPomodoroTimer,
                        onSendChatClick = ::sendChat,
                        onChatMessageInputChange = ::updateChatMessageInput
                    )
                }
            }.onFailure {
                postSideEffect(
                    RoomSideEffect.Message(
                        content = it.message,
                        defaultContentRes = R.string.failed_to_join_study_room
                    )
                )
                reduce { uiState.copy(joining = false) }
            }
        } catch (e: TimeoutCancellationException) {
            reduce {
                RoomUiState.WaitingRoom.FailedToConnect(
                    defaultMessageRes = R.string.timeout_to_join_study_room
                )
            }
        }
    }

    private fun onToggleAudio(audioTrack: AudioTrack?) = intent {
        if (state !is RoomUiState.StudyRoom) {
            return@intent
        }
        if (audioTrack != null) {
            roomService.produceAudio(audioTrack)
        } else {
            roomService.closeAudioProducer()
        }
    }

    private fun onToggleVideo(videoTrack: VideoTrack?) = intent {
        if (state !is RoomUiState.StudyRoom) {
            return@intent
        }
        if (videoTrack != null) {
            roomService.produceVideo(videoTrack)
        } else {
            roomService.closeVideoProducer()
        }
    }

    private fun onToggleHeadset(enabled: Boolean) = intent {
        if (enabled) {
            roomService.unmuteHeadset()
        } else {
            roomService.muteHeadset()
        }
    }

    private fun kickUser(userId: String) = intent {
        val uiState = state
        check(uiState is RoomUiState.StudyRoom)
        check(uiState.isCurrentUserMaster)
        roomService.kickUser(userId = userId)
    }

    private fun blockUser(userId: String) = intent {
        val uiState = state
        check(uiState is RoomUiState.StudyRoom)
        check(uiState.isCurrentUserMaster)
        roomService.blockUser(userId = userId)
    }

    private fun unblockUser(userId: String) = intent {
        val uiState = state
        check(uiState is RoomUiState.StudyRoom)
        check(uiState.isCurrentUserMaster)
        roomService.unblockUser(userId = userId)
            .onSuccess {
                reduce {
                    uiState.copy(
                        blacklist = uiState.blacklist.filterNot { it.id == userId }
                    )
                }
                postSideEffect(RoomSideEffect.Message(defaultContentRes = R.string.unblocked_user))
            }.onFailure {
                postSideEffect(
                    RoomSideEffect.Message(defaultContentRes = R.string.failed_to_unblock_user)
                )
            }
    }

    private fun updatePomodoroTimer(property: PomodoroTimerProperty) = intent {
        roomService.updateAndStopTimer(newProperty = property)
    }

    private fun startPomodoroTimer() = intent {
        roomService.startPomodoroTimer()
    }

    private fun updateChatMessageInput(message: String) = intent {
        val uiState = state
        check(uiState is RoomUiState.StudyRoom)
        reduce { uiState.copy(chatMessageInput = message) }
    }

    private fun sendChat(message: String) = intent {
        updateChatMessageInput("")
        roomService.sendChat(message)
    }

    fun updatePictureInPictureMode(isPipMode: Boolean) = intent {
        val uiState = state
        check(uiState is RoomUiState.StudyRoom)
        reduce { uiState.copy(isPipMode = isPipMode) }
    }

    private fun handleWaitingRoomEvent(waitingRoomEvent: WaitingRoomEvent) = intent {
        val uiState = state
        check(uiState is RoomUiState.WaitingRoom.Connected)
        when (waitingRoomEvent) {
            is WaitingRoomEvent.OtherPeerJoined -> {
                val newJoiner = waitingRoomEvent.joiner
                val newJoinerList = uiState.data.joinerList + newJoiner
                reduce {
                    uiState.copy(data = uiState.data.copy(joinerList = newJoinerList))
                }
            }
            is WaitingRoomEvent.OtherPeerExited -> {
                val exitedUserId = waitingRoomEvent.userId
                val newJoinerList = uiState.data.joinerList.filter { it.id != exitedUserId }
                reduce {
                    uiState.copy(data = uiState.data.copy(joinerList = newJoinerList))
                }
            }
        }
    }

    private fun handleStudyRoomEvent(studyRoomEvent: StudyRoomEvent) = intent {
        val uiState = state
        check(uiState is RoomUiState.StudyRoom)
        when (studyRoomEvent) {
            is StudyRoomEvent.OnChangePeerState -> {
                val existsState = uiState.peerStates.any { it.uid == studyRoomEvent.state.uid }
                if (existsState) {
                    val newPeerStates = uiState.peerStates.map { state ->
                        if (studyRoomEvent.state.uid == state.uid) {
                            return@map state.merge(studyRoomEvent.state)
                        }
                        return@map state
                    }
                    reduce { uiState.copy(peerStates = newPeerStates) }
                } else {
                    reduce {
                        uiState.copy(
                            peerStates = uiState.peerStates + studyRoomEvent.state.toInitialUiState(
                                isMe = currentUserId == studyRoomEvent.state.uid
                            )
                        )
                    }
                }
            }
            is StudyRoomEvent.AddedConsumer -> {
                val userId = studyRoomEvent.userId
                val track = studyRoomEvent.track
                val targetPeer = uiState.peerStates.find { it.uid == userId }
                check(targetPeer != null)
                val newPeer = when (track) {
                    is AudioTrack -> targetPeer.copy(audioTrack = track)
                    is VideoTrack -> targetPeer.copy(videoTrack = track)
                    else -> throw IllegalArgumentException()
                }
                val newPeerStates = uiState.peerStates.map { peerUiState ->
                    if (peerUiState.uid == userId) {
                        newPeer
                    } else {
                        peerUiState
                    }
                }
                reduce { uiState.copy(peerStates = newPeerStates) }
            }
            is StudyRoomEvent.OnCloseVideoConsumer -> {
                val newPeerStates = uiState.peerStates.map {
                    if (it.uid == studyRoomEvent.userId) {
                        return@map it.copy(videoTrack = null)
                    }
                    return@map it
                }
                reduce { uiState.copy(peerStates = newPeerStates) }
            }
            is StudyRoomEvent.OnCloseAudioConsumer -> {
                // TODO: 구현하기
            }
            is StudyRoomEvent.OnReceiveChatMessage -> {
                reduce {
                    uiState.copy(
                        chatMessages = listOf(studyRoomEvent.message) + uiState.chatMessages
                    )
                }
            }
            is StudyRoomEvent.TimerStateChanged -> {
                reduce {
                    uiState.copy(
                        pomodoroTimerEventDate = Clock.System.now().toLocalDateTime(
                            WebRtcServerTimeZone
                        ),
                        pomodoroTimerState = studyRoomEvent.state
                    )
                }
            }
            is StudyRoomEvent.TimerPropertyChanged -> {
                reduce {
                    uiState.copy(pomodoroTimer = studyRoomEvent.property)
                }
                postSideEffect(RoomSideEffect.Message(defaultContentRes = R.string.edited_timer))
            }
            is StudyRoomEvent.OnDisconnectPeer -> {
                val newPeerStates = uiState.peerStates.filter {
                    it.uid != studyRoomEvent.disposedPeerId
                }
                reduce { uiState.copy(peerStates = newPeerStates) }
            }
            is StudyRoomEvent.OnKicked -> onKickedUser(
                kickedUserId = studyRoomEvent.userId,
                uiState = uiState
            )
            is StudyRoomEvent.OnBlocked -> {
                val kickedUserId = studyRoomEvent.userId
                val kickedPeer = uiState.peerStates.find { it.uid == kickedUserId }
                check(kickedPeer != null)
                val newBlacklist = uiState.blacklist + PeerOverview(
                    id = kickedPeer.uid,
                    name = kickedPeer.name
                )
                onKickedUser(
                    kickedUserId = studyRoomEvent.userId,
                    uiState = uiState.copy(blacklist = newBlacklist)
                )
            }
        }
    }

    private fun onKickedUser(kickedUserId: String, uiState: RoomUiState.StudyRoom) = intent {
        val isMe = kickedUserId == currentUserId
        if (isMe) {
            reduce { uiState.copy(isCurrentUserKicked = true) }
        } else {
            val kickedUser = uiState.peerStates.find { it.uid == kickedUserId }
            kickedUser?.let { user ->
                postSideEffect(
                    RoomSideEffect.Message(
                        defaultContentRes = R.string.user_has_been_kicked,
                        stringResArgs = listOf(user.name)
                    )
                )
                reduce {
                    uiState.copy(
                        peerStates = uiState.peerStates.filter { it.uid != user.uid }
                    )
                }
            }
        }
    }

    override fun onCleared() {
        roomService.disconnect()
        mediaManager.disconnect()
    }
}
