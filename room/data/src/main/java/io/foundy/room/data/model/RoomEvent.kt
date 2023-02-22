package io.foundy.room.data.model

sealed class RoomEvent

sealed class WaitingRoomEvent : RoomEvent()
data class OtherPeerJoinedRoomEvent(val joiner: RoomJoiner) : WaitingRoomEvent()
data class OtherPeerExitedRoomEvent(val userId: String) : WaitingRoomEvent()

sealed class StudyRoomEvent : RoomEvent()
