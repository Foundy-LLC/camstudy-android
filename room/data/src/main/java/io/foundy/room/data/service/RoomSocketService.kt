package io.foundy.room.data.service

import io.foundy.room.data.model.OnCreated
import io.foundy.room.data.model.RoomEvent
import kotlinx.coroutines.flow.MutableStateFlow

class RoomSocketService : RoomService {

    override val event: MutableStateFlow<RoomEvent> = MutableStateFlow(OnCreated)
}
