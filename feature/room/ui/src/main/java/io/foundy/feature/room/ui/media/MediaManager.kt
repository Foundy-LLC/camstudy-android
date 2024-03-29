package io.foundy.feature.room.ui.media

import io.foundy.feature.room.ui.peer.PeerUiState
import kotlinx.coroutines.flow.Flow
import org.webrtc.AudioTrack
import org.webrtc.EglBase
import org.webrtc.VideoTrack

interface MediaManager {

    val mediaEvent: Flow<MediaManagerEvent>

    val eglBaseContext: EglBase.Context

    val enabledLocalVideo: Boolean
    val enabledLocalAudio: Boolean
    val enabledLocalHeadset: Boolean
    val currentUserState: PeerUiState

    val localVideoTrackFlow: Flow<VideoTrack?>
    val localAudioTrack: AudioTrack?

    fun onSessionScreenReady()
    fun toggleMicrophone(enabled: Boolean)
    fun toggleVideo(enabled: Boolean)
    fun switchCamera()
    fun toggleHeadset(enabled: Boolean)
    fun disconnect()
}
