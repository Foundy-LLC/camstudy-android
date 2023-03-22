package io.foundy.friend.data.repository

import androidx.paging.PagingData
import io.foundy.core.model.UserOverview
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    suspend fun getFriends(userId: String): Flow<PagingData<UserOverview>>
    suspend fun getFriendRequests(userId: String): Flow<PagingData<UserOverview>>
}
