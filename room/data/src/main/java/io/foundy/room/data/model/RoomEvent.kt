package io.foundy.room.data.model

import com.example.domain.PeerState
import org.webrtc.MediaStreamTrack

sealed class RoomEvent

sealed class WaitingRoomEvent : RoomEvent() {
    data class OtherPeerJoined(val joiner: RoomJoiner) : WaitingRoomEvent()
    data class OtherPeerExited(val userId: String) : WaitingRoomEvent()
}

sealed class StudyRoomEvent : RoomEvent() {
    data class OnChangePeerState(val state: PeerState) : StudyRoomEvent()
    data class AddedConsumer(val userId: String, val track: MediaStreamTrack) : StudyRoomEvent()
    data class OnDisconnectPeer(val disposedPeerId: String) : StudyRoomEvent()
}
