package io.foundy.room.data

import io.foundy.room.data.model.OnCreated
import io.foundy.room.data.model.RoomEvent
import io.foundy.room.data.service.RoomService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRoomService : RoomService {

    private val _event: MutableStateFlow<RoomEvent> = MutableStateFlow(OnCreated)
    override val event: Flow<RoomEvent> get() = _event
}
