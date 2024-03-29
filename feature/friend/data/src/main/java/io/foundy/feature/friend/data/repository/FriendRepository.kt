package io.foundy.feature.friend.data.repository

import androidx.paging.PagingData
import io.foundy.core.model.FriendStatus
import io.foundy.core.model.RecommendedUser
import io.foundy.core.model.UserOverview
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun getFriends(userId: String): Flow<PagingData<UserOverview>>
    fun getFriendRequests(userId: String): Flow<PagingData<UserOverview>>
    suspend fun getRecommendedFriends(userId: String): Result<List<RecommendedUser>>
    suspend fun requestFriend(targetUserId: String): Result<FriendStatus>
    suspend fun acceptFriendRequest(requesterId: String): Result<Unit>
    suspend fun rejectFriendRequest(requesterId: String): Result<Unit>
    suspend fun deleteFriend(targetUserId: String): Result<Unit>
}
