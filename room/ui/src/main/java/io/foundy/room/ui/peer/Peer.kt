package io.foundy.room.ui.peer

import com.example.domain.PeerState
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

data class PeerUiState(
    val uid: String,
    val name: String,
    val enabledMicrophone: Boolean,
    val enabledHeadset: Boolean,
    val audioTrack: AudioTrack? = null,
    val videoTrack: VideoTrack? = null
)

fun PeerState.toInitialUiState(): PeerUiState {
    return PeerUiState(
        uid = uid,
        name = name,
        enabledMicrophone = enabledMicrophone,
        enabledHeadset = enabledHeadset
    )
}

fun PeerUiState.merge(peerState: PeerState): PeerUiState {
    return copy(
        enabledHeadset = peerState.enabledHeadset,
        enabledMicrophone = peerState.enabledMicrophone
    )
}
