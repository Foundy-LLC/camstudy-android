package io.foundy.room.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.model.constant.RoomConstants
import io.foundy.room.data.model.StudyRoomEvent
import io.foundy.room.data.model.WaitingRoomEvent
import io.foundy.room.data.service.RoomService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomService: RoomService,
    private val authRepository: AuthRepository
) : ViewModel(), ContainerHost<RoomUiState, RoomSideEffect> {

    override val container: Container<RoomUiState, RoomSideEffect> =
        container(RoomUiState.WaitingRoom.Loading)

    private var _currentUserId: String? = null
    private val currentUserId: String get() = requireNotNull(_currentUserId)

    init {
        viewModelScope.launch {
            _currentUserId = authRepository.currentUserIdStream.first()
            check(_currentUserId != null) {
                "회원 정보를 얻을 수 없습니다. 로그인하지 않고 공부방 접속은 할 수 없습니다."
            }
        }
        viewModelScope.launch {
            roomService.event.collect {
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
                    }
                )
            }
        } catch (e: TimeoutCancellationException) {
            reduce { RoomUiState.WaitingRoom.FailedToConnect(R.string.failed_to_join_waiting_room) }
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

    private fun handleStudyRoomEvent(studyRoomEvent: StudyRoomEvent) {}
}
