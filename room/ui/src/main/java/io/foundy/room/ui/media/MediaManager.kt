package io.foundy.room.ui.media

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import io.foundy.room.ui.audio.AudioHandler
import io.foundy.room.ui.audio.AudioSwitchHandler
import io.foundy.room.ui.peer.PeerConnectionFactoryWrapper
import io.getstream.log.taggedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.webrtc.AudioTrack
import org.webrtc.Camera2Capturer
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerationAndroid
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoTrack
import java.util.UUID

val LocalMediaManager: ProvidableCompositionLocal<MediaManager> =
    staticCompositionLocalOf { error("WebRtcSessionManager was not initialized!") }

@Composable
fun rememberMediaManager(): MediaManager {
    val context = LocalContext.current
    return remember {
        MediaManager(
            context = context,
            peerConnectionFactory = PeerConnectionFactoryWrapper(context = context)
        )
    }
}

class MediaManager(
    private val context: Context,
    private val peerConnectionFactory: PeerConnectionFactoryWrapper
) {
    private val logger by taggedLogger("Call:LocalRoomSessionManager")
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val eglBaseContext: EglBase.Context get() = peerConnectionFactory.eglBaseContext

    var enabledLocalVideo by mutableStateOf(false)
        private set

    var enabledLocalAudio by mutableStateOf(false)
        private set

    // used to send local video track to the fragment
    private val _localVideoSinkFlow = MutableSharedFlow<VideoTrack?>(replay = 1)
    val localVideoTrackFlow: SharedFlow<VideoTrack?> = _localVideoSinkFlow

    // used to send remote video track to the sender
    private val _remoteVideoSinkFlow = MutableSharedFlow<VideoTrack>(replay = 1)
    val remoteVideoTrackFlow: SharedFlow<VideoTrack> = _remoteVideoSinkFlow

    // getting front camera
    private val videoCapturer: VideoCapturer by lazy { buildCameraCapturer() }
    private val cameraManager by lazy { context.getSystemService<CameraManager>() }
    private val cameraEnumerator: Camera2Enumerator by lazy {
        Camera2Enumerator(context)
    }

    private val resolution: CameraEnumerationAndroid.CaptureFormat
        get() {
            val frontCamera = cameraEnumerator.deviceNames.first { cameraName ->
                cameraEnumerator.isFrontFacing(cameraName)
            }
            val supportedFormats = cameraEnumerator.getSupportedFormats(frontCamera) ?: emptyList()
            return supportedFormats.firstOrNull {
                (it.width == 720 || it.width == 480 || it.width == 360)
            } ?: error("There is no matched resolution!")
        }

    // we need it to initialize video capturer
    private val surfaceTextureHelper = SurfaceTextureHelper.create(
        "SurfaceTextureHelperThread",
        peerConnectionFactory.eglBaseContext
    )

    private val videoSource by lazy {
        peerConnectionFactory.makeVideoSource(videoCapturer.isScreencast).apply {
            videoCapturer.initialize(surfaceTextureHelper, context, this.capturerObserver)
            videoCapturer.startCapture(resolution.width, resolution.height, 30)
        }
    }

    private val localVideoTrack: VideoTrack by lazy {
        peerConnectionFactory.makeVideoTrack(
            source = videoSource,
            trackId = "Video${UUID.randomUUID()}"
        )
    }

    /* Audio properties */

    private val audioHandler: AudioHandler by lazy {
        AudioSwitchHandler(context)
    }

    private val audioManager by lazy {
        context.getSystemService<AudioManager>()
    }

    private val audioConstraints: MediaConstraints by lazy {
        buildAudioConstraints()
    }

    private val audioSource by lazy {
        peerConnectionFactory.makeAudioSource(audioConstraints)
    }

    val localAudioTrack: AudioTrack by lazy {
        peerConnectionFactory.makeAudioTrack(
            source = audioSource,
            trackId = "Audio${UUID.randomUUID()}"
        )
    }

    fun onSessionScreenReady() {
        setupAudio()
        managerScope.launch {
            // sending local video track to show local video from start
            _localVideoSinkFlow.emit(localVideoTrack)
            enabledLocalVideo = true
        }
    }

    private fun buildCameraCapturer(): VideoCapturer {
        val manager = cameraManager ?: throw RuntimeException("CameraManager was not initialized!")

        val ids = manager.cameraIdList
        var foundCamera = false
        var cameraId = ""

        for (id in ids) {
            val characteristics = manager.getCameraCharacteristics(id)
            val cameraLensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

            if (cameraLensFacing == CameraMetadata.LENS_FACING_FRONT) {
                foundCamera = true
                cameraId = id
            }
        }

        if (!foundCamera && ids.isNotEmpty()) {
            cameraId = ids.first()
        }

        return Camera2Capturer(context, cameraId, null)
    }

    private fun buildAudioConstraints(): MediaConstraints {
        val mediaConstraints = MediaConstraints()
        val items = listOf(
            MediaConstraints.KeyValuePair(
                "googEchoCancellation",
                true.toString()
            ),
            MediaConstraints.KeyValuePair(
                "googAutoGainControl",
                true.toString()
            ),
            MediaConstraints.KeyValuePair(
                "googHighpassFilter",
                true.toString()
            ),
            MediaConstraints.KeyValuePair(
                "googNoiseSuppression",
                true.toString()
            ),
            MediaConstraints.KeyValuePair(
                "googTypingNoiseDetection",
                true.toString()
            )
        )

        return mediaConstraints.apply {
            with(optional) {
                add(MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"))
                addAll(items)
            }
        }
    }

    private fun setupAudio() {
        logger.d { "[setupAudio] #sfu; no args" }
        audioHandler.start()
        audioManager?.isMicrophoneMute = true
        audioManager?.mode = AudioManager.MODE_IN_COMMUNICATION

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val devices = audioManager?.availableCommunicationDevices ?: return
            val deviceType = AudioDeviceInfo.TYPE_BUILTIN_SPEAKER

            val device = devices.firstOrNull { it.type == deviceType } ?: return

            val isCommunicationDeviceSet = audioManager?.setCommunicationDevice(device)
            logger.d { "[setupAudio] #sfu; isCommunicationDeviceSet: $isCommunicationDeviceSet" }
        }
    }

    fun toggleMicrophone(enabled: Boolean) {
        audioManager?.isMicrophoneMute = !enabled
        enabledLocalAudio = enabled
    }

    fun toggleVideo(enabled: Boolean) {
        if (enabled) {
            enabledLocalVideo = true
            videoCapturer.startCapture(resolution.width, resolution.height, 30)
        } else {
            enabledLocalVideo = false
            videoCapturer.stopCapture()
        }
    }

    fun disconnect() {
        // dispose audio & video tracks.
        for (videoTrack in remoteVideoTrackFlow.replayCache) {
            videoTrack.dispose()
        }
        for (videoTrack in localVideoTrackFlow.replayCache) {
            videoTrack?.dispose()
        }
        localAudioTrack.dispose()

        // dispose audio handler and video capturer.
        audioHandler.stop()
        videoCapturer.stopCapture()
        videoCapturer.dispose()
    }
}
