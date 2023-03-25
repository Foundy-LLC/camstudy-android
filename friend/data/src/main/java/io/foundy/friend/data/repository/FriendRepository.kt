package io.foundy.friend.data.repository

import androidx.paging.PagingData
import io.foundy.core.model.UserOverview
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun getFriends(userId: String): Flow<PagingData<UserOverview>>
    fun getFriendRequests(userId: String): Flow<PagingData<UserOverview>>
    suspend fun requestFriend(targetUserId: String): Result<Unit>
    suspend fun acceptFriendRequest(requesterId: String): Result<Unit>
    suspend fun deleteFriend(targetUserId: String): Result<Unit>
}
