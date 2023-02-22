package io.foundy.room.ui

import androidx.annotation.StringRes
import io.foundy.room.data.model.WaitingRoomData

sealed class RoomUiState {

    object Connecting : RoomUiState()

    data class FailedToConnect(@StringRes val messageRes: Int) : RoomUiState()

    data class WaitingRoom(val data: WaitingRoomData) : RoomUiState()
}
