package io.foundy.room.ui.fake

import io.foundy.room.data.model.JoinRoomSuccessResponse
import io.foundy.room.data.model.RoomEvent
import io.foundy.room.data.model.WaitingRoomData
import io.foundy.room.data.service.RoomService
import kotlinx.coroutines.flow.MutableSharedFlow
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

class FakeRoomService : RoomService {

    override val eventFlow: MutableSharedFlow<RoomEvent> = MutableSharedFlow()

    var waitingRoomData: WaitingRoomData = WaitingRoomData(
        joinerList = emptyList(),
        capacity = 4,
        masterId = "1",
        blacklist = emptyList(),
        hasPassword = false
    )

    var onConnect: suspend () -> Unit = {}

    override suspend fun connect() {
        onConnect()
        return
    }

    override suspend fun joinToWaitingRoom(roomId: String): WaitingRoomData {
        return waitingRoomData
    }

    override suspend fun joinToStudyRoom(
        localVideo: VideoTrack?,
        localAudio: AudioTrack?,
        userId: String,
        password: String
    ): Result<JoinRoomSuccessResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun produceVideo(videoTrack: VideoTrack) {
        TODO("Not yet implemented")
    }

    override suspend fun closeVideoProducer() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }
}
