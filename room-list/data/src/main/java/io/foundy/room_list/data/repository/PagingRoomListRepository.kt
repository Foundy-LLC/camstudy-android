package io.foundy.room_list.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.foundy.core.model.RoomOverview
import io.foundy.room_list.data.api.RoomListApi
import io.foundy.room_list.data.source.RoomPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PagingRoomListRepository @Inject constructor(
    private val roomListApi: RoomListApi
) : RoomListRepository {

    override suspend fun getRooms(): Flow<PagingData<RoomOverview>> {
        return Pager(
            config = PagingConfig(RoomPagingSource.PAGE_SIZE),
            pagingSourceFactory = { RoomPagingSource(api = roomListApi) }
        ).flow
    }
}
