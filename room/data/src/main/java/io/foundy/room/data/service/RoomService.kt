package io.foundy.room.data.service

import io.foundy.room.data.model.RoomEvent
import kotlinx.coroutines.flow.Flow

interface RoomService {
    val event: Flow<RoomEvent>
}
