package io.foundy.room.data.service

import io.foundy.room.data.model.OnCreated
import io.foundy.room.data.model.RoomEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RoomSocketService : RoomService {

    private val _event: MutableStateFlow<RoomEvent> = MutableStateFlow(OnCreated)
    override val event: Flow<RoomEvent> get() = _event
}
