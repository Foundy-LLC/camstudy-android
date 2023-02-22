package io.foundy.room.data

import io.foundy.room.data.model.RoomEvent
import io.foundy.room.data.model.WaitingRoomData
import io.foundy.room.data.service.RoomService
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeRoomService : RoomService {

    override val event: MutableSharedFlow<RoomEvent> = MutableSharedFlow()

    override suspend fun connect() {
        TODO("Not yet implemented")
    }

    override suspend fun joinToWaitingRoom(roomId: String): WaitingRoomData {
        TODO("Not yet implemented")
    }
}
