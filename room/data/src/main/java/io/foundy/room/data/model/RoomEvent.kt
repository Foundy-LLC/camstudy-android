package io.foundy.room.data.model

import org.webrtc.MediaStreamTrack

sealed class RoomEvent

sealed class WaitingRoomEvent : RoomEvent() {
    data class OtherPeerJoined(val joiner: RoomJoiner) : WaitingRoomEvent()
    data class OtherPeerExited(val userId: String) : WaitingRoomEvent()
}

sealed class StudyRoomEvent : RoomEvent() {
    data class AddedConsumer(val userId: String, val track: MediaStreamTrack) : StudyRoomEvent()
}
