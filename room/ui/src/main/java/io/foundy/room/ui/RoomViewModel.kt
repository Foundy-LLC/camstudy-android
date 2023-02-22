package io.foundy.room.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.room.data.service.RoomService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.collectLatest
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
            roomService.event.collectLatest {
                TODO()
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
}
