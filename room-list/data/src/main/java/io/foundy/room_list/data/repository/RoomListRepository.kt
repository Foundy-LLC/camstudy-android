package io.foundy.room_list.data.repository

import androidx.paging.PagingData
import io.foundy.core.model.RoomOverview
import io.foundy.room_list.data.model.RoomCreateRequestBody
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RoomListRepository {

    fun getRooms(query: String): Flow<PagingData<RoomOverview>>

    suspend fun getRecentRooms(userId: String): Result<List<RoomOverview>>

    suspend fun createRoom(
        createRequestBody: RoomCreateRequestBody,
        thumbnail: File?
    ): Result<RoomOverview>
}
