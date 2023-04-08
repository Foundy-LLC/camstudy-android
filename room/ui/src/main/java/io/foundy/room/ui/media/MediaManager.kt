package io.foundy.room.ui.media

import kotlinx.coroutines.flow.Flow
import org.webrtc.AudioTrack
import org.webrtc.EglBase
import org.webrtc.VideoTrack

interface MediaManager {

    val eglBaseContext: EglBase.Context

    val enabledLocalVideo: Boolean
    val enabledLocalAudio: Boolean
    val enabledLocalHeadset: Boolean

    val localVideoTrackFlow: Flow<VideoTrack?>
    val localAudioTrack: AudioTrack?

    fun onSessionScreenReady()
    fun toggleMicrophone(enabled: Boolean)
    fun toggleVideo(enabled: Boolean)
    fun switchCamera()
    fun toggleHeadset(enabled: Boolean)
    fun disconnect()
}
