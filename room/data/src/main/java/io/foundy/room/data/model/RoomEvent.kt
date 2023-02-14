package io.foundy.room.data.model

sealed class RoomEvent

object OnCreated : RoomEvent()

data class OnConnectedWaitingRoom(val data: WaitingRoomData) : RoomEvent()
