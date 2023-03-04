package io.foundy.room.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.model.constant.RoomConstants
import io.foundy.room.data.model.StudyRoomEvent
import io.foundy.room.data.model.WaitingRoomEvent
import io.foundy.room.data.service.RoomService
import io.foundy.room.ui.R
import io.foundy.room.ui.peer.merge
import io.foundy.room.ui.peer.toInitialUiState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomService: RoomService,
    private val authRepository: AuthRepository,
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
    }

    fun connect(roomId: String) = intent {
        try {
            roomService.connect()
            joinToWaitingRoom(roomId)
        } catch (e: TimeoutCancellationException) {
            reduce { RoomUiState.WaitingRoom.FailedToConnect(R.string.failed_to_connect_server) }
        }
    }

    private fun joinToWaitingRoom(roomId: String) = intent {
        try {
            val waitingRoomData = roomService.joinToWaitingRoom(roomId)
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
            reduce { RoomUiState.WaitingRoom.FailedToConnect(R.string.failed_to_join_waiting_room) }
        }
    }

    private fun joinToStudyRoom(
        localVideo: VideoTrack?,
        localAudio: AudioTrack?,
        password: String
    ) = intent {
        try {
            roomService.joinToStudyRoom(
                localVideo = localVideo,
                localAudio = localAudio,
                userId = currentUserId,
                password = password
            ).onSuccess {
                reduce {
                    RoomUiState.StudyRoom(
                        peerStates = it.peerStates.map { peerState ->
                            peerState.toInitialUiState()
                        },
                        pomodoroTimer = it.timerProperty,
                        pomodoroTimerState = it.timerState
                    )
                }
            }.onFailure {
                postSideEffect(
                    RoomSideEffect.Message(
                        content = it.message,
                        defaultContentRes = R.string.failed_to_join_study_room
                    )
                )
            }
        } catch (e: TimeoutCancellationException) {
            reduce { RoomUiState.WaitingRoom.FailedToConnect(R.string.timeout_to_join_study_room) }
        }
    }

    fun onToggleVideo(videoTrack: VideoTrack?) = intent {
        if (state !is RoomUiState.StudyRoom) {
            return@intent
        }
        if (videoTrack != null) {
            roomService.produceVideo(videoTrack)
        } else {
            roomService.closeVideoProducer()
        }
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
                            peerStates = uiState.peerStates +
                                studyRoomEvent.state.toInitialUiState()
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
            is StudyRoomEvent.OnDisconnectPeer -> {
                val newPeerStates = uiState.peerStates.filter {
                    it.uid != studyRoomEvent.disposedPeerId
                }
                reduce { uiState.copy(peerStates = newPeerStates) }
            }
        }
    }

    override fun onCleared() {
        roomService.disconnect()
    }
}
