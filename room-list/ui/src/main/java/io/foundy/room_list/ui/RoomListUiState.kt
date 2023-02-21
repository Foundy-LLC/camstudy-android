package io.foundy.room_list.ui

import androidx.paging.PagingData
import io.foundy.core.model.RoomOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class RoomListUiState(
    val roomPagingDataStream: Flow<PagingData<RoomOverview>> = emptyFlow()
)
