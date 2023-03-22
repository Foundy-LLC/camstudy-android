package io.foundy.friend.ui

import androidx.paging.PagingData
import io.foundy.core.model.UserOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class FriendUiState(
    val pagingDataFlow: Flow<PagingData<UserOverview>> = emptyFlow()
)
