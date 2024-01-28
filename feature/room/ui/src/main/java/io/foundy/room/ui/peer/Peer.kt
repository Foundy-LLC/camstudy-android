package io.foundy.room.ui.peer

import io.foundy.room.domain.PeerState
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

data class PeerUiState(
    val uid: String,
    val name: String,
    val isMe: Boolean,
    val enabledMicrophone: Boolean,
    val enabledHeadset: Boolean,
    val audioTrack: AudioTrack? = null,
    val videoTrack: VideoTrack? = null
)

fun PeerState.toInitialUiState(isMe: Boolean): PeerUiState {
    return PeerUiState(
        uid = uid,
        isMe = isMe,
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
