package io.foundy.feature.room.data.service

import io.foundy.feature.room.data.model.JoinRoomSuccessResponse
import io.foundy.feature.room.data.model.RoomEvent
import io.foundy.feature.room.data.model.WaitingRoomData
import io.foundy.feature.room.domain.PomodoroTimerProperty
import kotlinx.coroutines.flow.Flow
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

interface RoomService {
    val eventFlow: Flow<RoomEvent>

    /**
     * 공부방 소켓 서버에 연결을 시도한다.
     */
    suspend fun connect(roomId: String)

    /**
     * 대기실에 입장한다.
     */
    suspend fun joinToWaitingRoom(roomId: String): WaitingRoomData?

    /**
     * 공부방에 입장한다.
     */
    suspend fun joinToStudyRoom(
        localVideo: VideoTrack?,
        localAudio: AudioTrack?,
        userId: String,
        password: String
    ): Result<JoinRoomSuccessResponse>

    suspend fun produceVideo(videoTrack: VideoTrack)

    suspend fun closeVideoProducer()

    suspend fun produceAudio(audioTrack: AudioTrack)

    suspend fun closeAudioProducer()

    suspend fun muteHeadset()

    suspend fun unmuteHeadset()

    suspend fun startPomodoroTimer()

    suspend fun sendChat(message: String)

    suspend fun kickUser(userId: String)

    suspend fun blockUser(userId: String)

    suspend fun unblockUser(userId: String): Result<Unit>

    suspend fun updateAndStopTimer(newProperty: PomodoroTimerProperty)

    fun disconnect()
}
