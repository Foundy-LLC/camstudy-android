package io.foundy.feature.room_list.ui

import androidx.paging.PagingData
import io.foundy.core.model.RoomOverview
import kotlinx.coroutines.flow.Flow

data class RoomListUiState(
    val roomPagingDataStream: Flow<PagingData<RoomOverview>>,
    val searchQuery: String = "",
    val onSearchQueryChange: (String) -> Unit,
    val onRefresh: () -> Unit
)
