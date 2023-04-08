package io.foundy.room.ui.media

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.webrtc.AudioTrack
import org.webrtc.EglBase
import org.webrtc.VideoTrack

class FakeMediaManager : MediaManager {

    override val eglBaseContext: EglBase.Context
        get() = TODO("Not yet implemented")
    override val enabledLocalVideo: Boolean = true
    override val enabledLocalAudio: Boolean = true
    override val enabledLocalHeadset: Boolean = true
    override val localVideoTrackFlow: Flow<VideoTrack?> = emptyFlow()
    override val localAudioTrack: AudioTrack? = null

    override fun onSessionScreenReady() {
        TODO("Not yet implemented")
    }

    override fun toggleMicrophone(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun toggleVideo(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun switchCamera() {
        TODO("Not yet implemented")
    }

    override fun toggleHeadset(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }
}
