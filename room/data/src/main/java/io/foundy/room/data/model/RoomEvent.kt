package io.foundy.room.data.model

import com.example.domain.ChatMessage
import com.example.domain.PeerState
import com.example.domain.PomodoroTimerProperty
import com.example.domain.PomodoroTimerState
import org.webrtc.MediaStreamTrack

sealed class RoomEvent

sealed class WaitingRoomEvent : RoomEvent() {
    data class OtherPeerJoined(val joiner: RoomJoiner) : WaitingRoomEvent()
    data class OtherPeerExited(val userId: String) : WaitingRoomEvent()
}

sealed class StudyRoomEvent : RoomEvent() {
    data class OnChangePeerState(val state: PeerState) : StudyRoomEvent()
    data class AddedConsumer(val userId: String, val track: MediaStreamTrack) : StudyRoomEvent()
    data class OnCloseVideoConsumer(val userId: String) : StudyRoomEvent()
    data class OnCloseAudioConsumer(val userId: String) : StudyRoomEvent()
    data class OnReceiveChatMessage(val message: ChatMessage) : StudyRoomEvent()
    data class TimerStateChanged(val state: PomodoroTimerState) : StudyRoomEvent()
    data class TimerPropertyChanged(val property: PomodoroTimerProperty) : StudyRoomEvent()
    data class OnDisconnectPeer(val disposedPeerId: String) : StudyRoomEvent()
    data class OnKicked(val userId: String) : StudyRoomEvent()
    data class OnBlocked(val userId: String) : StudyRoomEvent()
}
