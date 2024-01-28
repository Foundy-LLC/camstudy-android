/*
 * Copyright 2023 Stream.IO, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.foundy.room.ui.peer

import android.content.Context
import android.os.Build
import io.getstream.log.taggedLogger
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnectionFactory
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import org.webrtc.audio.JavaAudioDeviceModule

class PeerConnectionFactoryWrapper constructor(
    private val context: Context
) {
    private val webRtcLogger by taggedLogger("Call:WebRTC")
    private val audioLogger by taggedLogger("Call:AudioTrackCallback")

    val eglBaseContext: EglBase.Context by lazy {
        EglBase.create().eglBaseContext
    }

    /**
     * Default video decoder factory used to unpack video from the remote tracks.
     */
    private val videoDecoderFactory by lazy {
        DefaultVideoDecoderFactory(
            eglBaseContext
        )
    }

    private val videoEncoderFactory by lazy {
        DefaultVideoEncoderFactory(
            eglBaseContext,
            true,
            true
        )
    }

    /**
     * Factory that builds all the connections based on the extensive configuration provided under
     * the hood.
     */
    private val factory by lazy {
        PeerConnectionFactory.builder()
            .setVideoDecoderFactory(videoDecoderFactory)
            .setVideoEncoderFactory(videoEncoderFactory)
            .setAudioDeviceModule(
                JavaAudioDeviceModule.builder(context)
                    .setUseHardwareAcousticEchoCanceler(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    )
                    .setUseHardwareNoiseSuppressor(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    .setAudioRecordErrorCallback(
                        object : JavaAudioDeviceModule.AudioRecordErrorCallback {
                            override fun onWebRtcAudioRecordInitError(p0: String?) {
                                audioLogger.w { "[onWebRtcAudioRecordInitError] $p0" }
                            }

                            override fun onWebRtcAudioRecordStartError(
                                p0: JavaAudioDeviceModule.AudioRecordStartErrorCode?,
                                p1: String?
                            ) {
                                audioLogger.w { "[onWebRtcAudioRecordInitError] $p1" }
                            }

                            override fun onWebRtcAudioRecordError(p0: String?) {
                                audioLogger.w { "[onWebRtcAudioRecordError] $p0" }
                            }
                        })
                    .setAudioTrackErrorCallback(
                        object : JavaAudioDeviceModule.AudioTrackErrorCallback {
                            override fun onWebRtcAudioTrackInitError(p0: String?) {
                                audioLogger.w { "[onWebRtcAudioTrackInitError] $p0" }
                            }

                            override fun onWebRtcAudioTrackStartError(
                                p0: JavaAudioDeviceModule.AudioTrackStartErrorCode?,
                                p1: String?
                            ) {
                                audioLogger.w { "[onWebRtcAudioTrackStartError] $p0" }
                            }

                            override fun onWebRtcAudioTrackError(p0: String?) {
                                audioLogger.w { "[onWebRtcAudioTrackError] $p0" }
                            }
                        })
                    .setAudioRecordStateCallback(
                        object : JavaAudioDeviceModule.AudioRecordStateCallback {
                            override fun onWebRtcAudioRecordStart() {
                                audioLogger.d { "[onWebRtcAudioRecordStart] no args" }
                            }

                            override fun onWebRtcAudioRecordStop() {
                                audioLogger.d { "[onWebRtcAudioRecordStop] no args" }
                            }
                        })
                    .setAudioTrackStateCallback(
                        object : JavaAudioDeviceModule.AudioTrackStateCallback {
                            override fun onWebRtcAudioTrackStart() {
                                audioLogger.d { "[onWebRtcAudioTrackStart] no args" }
                            }

                            override fun onWebRtcAudioTrackStop() {
                                audioLogger.d { "[onWebRtcAudioTrackStop] no args" }
                            }
                        })
                    .createAudioDeviceModule().also {
                        it.setMicrophoneMute(false)
                        it.setSpeakerMute(false)
                    }
            )
            .createPeerConnectionFactory()
    }

    /**
     * Builds a [VideoSource] from the [factory] that can be used for regular video share (camera)
     * or screen sharing.
     *
     * @param isScreencast If we're screen sharing using this source.
     * @return [VideoSource] that can be used to build tracks.
     */
    fun makeVideoSource(isScreencast: Boolean): VideoSource =
        factory.createVideoSource(isScreencast)

    /**
     * Builds a [VideoTrack] from the [factory] that can be used for regular video share (camera)
     * or screen sharing.
     *
     * @param source The [VideoSource] used for the track.
     * @param trackId The unique ID for this track.
     * @return [VideoTrack] That represents a video feed.
     */
    fun makeVideoTrack(
        source: VideoSource,
        trackId: String
    ): VideoTrack = factory.createVideoTrack(trackId, source)

    /**
     * Builds an [AudioSource] from the [factory] that can be used for audio sharing.
     *
     * @param constraints The constraints used to change the way the audio behaves.
     * @return [AudioSource] that can be used to build tracks.
     */
    fun makeAudioSource(constraints: MediaConstraints = MediaConstraints()): AudioSource =
        factory.createAudioSource(constraints)

    /**
     * Builds an [AudioTrack] from the [factory] that can be used for regular video share (camera)
     * or screen sharing.
     *
     * @param source The [AudioSource] used for the track.
     * @param trackId The unique ID for this track.
     * @return [AudioTrack] That represents an audio feed.
     */
    fun makeAudioTrack(
        source: AudioSource,
        trackId: String
    ): AudioTrack = factory.createAudioTrack(trackId, source)
}
