package io.foundy.room.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.room.data.model.StudyRoomEvent
import io.foundy.room.data.model.WaitingRoomEvent
import io.foundy.room.data.service.RoomService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomService: RoomService
) : ViewModel(), ContainerHost<RoomUiState, RoomSideEffect> {

    override val container: Container<RoomUiState, RoomSideEffect> =
        container(RoomUiState.Connecting)

    init {
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
            reduce { RoomUiState.FailedToConnect(R.string.failed_to_connect_server) }
        }
    }

    private fun joinToWaitingRoom(roomId: String) = intent {
        try {
            val waitingRoomData = roomService.joinToWaitingRoom(roomId)
            reduce { RoomUiState.WaitingRoom(data = waitingRoomData) }
        } catch (e: TimeoutCancellationException) {
            reduce { RoomUiState.FailedToConnect(R.string.failed_to_join_waiting_room) }
        }
    }

    private fun handleWaitingRoomEvent(waitingRoomEvent: WaitingRoomEvent) = intent {
        val uiState = state
        check(uiState is RoomUiState.WaitingRoom)
        when (waitingRoomEvent) {
            is WaitingRoomEvent.OtherPeerJoined -> {
                val newJoiner = waitingRoomEvent.joiner
                val newJoinerList = uiState.data.joinerList + newJoiner
                reduce {
                    RoomUiState.WaitingRoom(data = uiState.data.copy(joinerList = newJoinerList))
                }
            }
            is WaitingRoomEvent.OtherPeerExited -> {
                val exitedUserId = waitingRoomEvent.userId
                val newJoinerList = uiState.data.joinerList.filter { it.id != exitedUserId }
                reduce {
                    RoomUiState.WaitingRoom(data = uiState.data.copy(joinerList = newJoinerList))
                }
            }
        }
    }

    private fun handleStudyRoomEvent(studyRoomEvent: StudyRoomEvent) {}
}
