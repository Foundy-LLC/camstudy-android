package io.foundy.room.ui.media

import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

sealed class MediaManagerEvent {
    data class ToggleVideo(val track: VideoTrack?) : MediaManagerEvent()
    data class ToggleAudio(val track: AudioTrack?) : MediaManagerEvent()
    data class ToggleHeadset(val enabled: Boolean) : MediaManagerEvent()
}
