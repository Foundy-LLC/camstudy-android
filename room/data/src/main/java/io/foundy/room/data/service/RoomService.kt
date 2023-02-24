package io.foundy.room.data.service

import io.foundy.room.data.model.JoinRoomSuccessResponse
import io.foundy.room.data.model.RoomEvent
import io.foundy.room.data.model.WaitingRoomData
import kotlinx.coroutines.flow.Flow
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

interface RoomService {
    val event: Flow<RoomEvent>

    /**
     * 공부방 소켓 서버에 연결을 시도한다.
     */
    suspend fun connect()

    /**
     * 대기실에 입장한다.
     */
    suspend fun joinToWaitingRoom(roomId: String): WaitingRoomData

    /**
     * 공부방에 입장한다.
     */
    suspend fun joinToStudyRoom(
        localVideo: VideoTrack?,
        localAudio: AudioTrack?,
        userId: String,
        password: String
    ): Result<JoinRoomSuccessResponse>
}
