package io.foundy.room.data.model

sealed class RoomEvent

data class OnConnectedWaitingRoom(val data: WaitingRoomData) : RoomEvent()
