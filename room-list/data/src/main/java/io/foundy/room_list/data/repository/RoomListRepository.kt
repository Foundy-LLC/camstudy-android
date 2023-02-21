package io.foundy.room_list.data.repository

import androidx.paging.PagingData
import io.foundy.core.model.RoomOverview
import kotlinx.coroutines.flow.Flow

interface RoomListRepository {
    fun getRooms(): Flow<PagingData<RoomOverview>>
}
