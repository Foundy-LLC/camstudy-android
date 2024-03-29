package io.foundy.feature.room.ui.media

import io.foundy.feature.room.ui.peer.PeerUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.webrtc.AudioTrack
import org.webrtc.EglBase
import org.webrtc.VideoTrack
import javax.inject.Inject

class FakeMediaManager @Inject constructor() : MediaManager {

    override val mediaEvent: Flow<MediaManagerEvent> = emptyFlow()

    override val eglBaseContext: EglBase.Context = EglBase.Context { 10 }
    override val enabledLocalVideo: Boolean = true
    override val enabledLocalAudio: Boolean = true
    override val enabledLocalHeadset: Boolean = true
    override val currentUserState: PeerUiState = PeerUiState(
        uid = "uid",
        name = "김민성",
        enabledMicrophone = enabledLocalAudio,
        enabledHeadset = enabledLocalHeadset,
        isMe = false
    )
    override val localVideoTrackFlow: Flow<VideoTrack?> = emptyFlow()
    override val localAudioTrack: AudioTrack? = null

    var didDisconnect: Boolean = false
        private set

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
        didDisconnect = true
    }
}
