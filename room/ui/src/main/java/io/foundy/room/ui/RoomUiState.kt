package io.foundy.room.ui

import androidx.annotation.StringRes
import io.foundy.room.data.model.WaitingRoomData

sealed class RoomUiState {

    object Connecting : RoomUiState()

    data class FailedToConnect(@StringRes val messageRes: Int) : RoomUiState()

    // TODO: Connecting이랑 Failure 둘다 여기에 넣기
    data class WaitingRoom(val data: WaitingRoomData) : RoomUiState()
}
