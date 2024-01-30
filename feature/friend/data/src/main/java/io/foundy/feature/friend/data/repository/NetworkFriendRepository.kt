package io.foundy.feature.friend.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.data.model.toEntity
import io.foundy.core.model.FriendStatus
import io.foundy.core.model.RecommendedUser
import io.foundy.core.model.UserOverview
import io.foundy.feature.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.feature.friend.data.api.FriendApi
import io.foundy.feature.friend.data.api.RecommendUserApi
import io.foundy.feature.friend.data.model.FriendPostRequestBody
import io.foundy.feature.friend.data.model.toEntity
import io.foundy.feature.friend.data.source.FriendPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkFriendRepository @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val friendApi: FriendApi,
    private val recommendUserApi: RecommendUserApi
) : FriendRepository {

    private suspend fun requireCurrentUserId(): String {
        val currentUserId = getCurrentUserIdUseCase()
        check(currentUserId != null)
        return currentUserId
    }

    override fun getFriends(userId: String): Flow<PagingData<UserOverview>> {
        return Pager(
            config = PagingConfig(FriendPagingSource.PAGE_SIZE),
            pagingSourceFactory = {
                FriendPagingSource(
                    api = friendApi,
                    userId = userId,
                    accepted = true
                )
            }
        ).flow
    }

    override fun getFriendRequests(userId: String): Flow<PagingData<UserOverview>> {
        return Pager(
            config = PagingConfig(FriendPagingSource.PAGE_SIZE),
            pagingSourceFactory = {
                FriendPagingSource(
                    api = friendApi,
                    userId = userId,
                    accepted = false
                )
            }
        ).flow
    }

    override suspend fun getRecommendedFriends(userId: String): Result<List<RecommendedUser>> {
        return runCatching {
            val response = recommendUserApi.getRecommendedFriends(userId = userId)
            response.getDataOrThrowMessage().users.map { it.toEntity() }
        }
    }

    override suspend fun requestFriend(targetUserId: String): Result<FriendStatus> {
        val currentUserId = requireCurrentUserId()
        return runCatching {
            val response = friendApi.requestFriend(
                requesterId = currentUserId,
                body = FriendPostRequestBody(targetUserId = targetUserId)
            )
            response.getDataOrThrowMessage().toEntity()
        }
    }

    override suspend fun acceptFriendRequest(requesterId: String): Result<Unit> {
        val currentUserId = requireCurrentUserId()
        return runCatching {
            val response = friendApi.acceptRequest(userId = currentUserId, friendId = requesterId)
            return@runCatching response.getDataOrThrowMessage()
        }
    }

    override suspend fun rejectFriendRequest(requesterId: String): Result<Unit> {
        val currentUserId = requireCurrentUserId()
        return runCatching {
            val response =
                friendApi.deleteFriend(requesterId = requesterId, acceptorId = currentUserId)
            return@runCatching response.getDataOrThrowMessage()
        }
    }

    override suspend fun deleteFriend(targetUserId: String): Result<Unit> {
        val currentUserId = requireCurrentUserId()
        return runCatching {
            val response =
                friendApi.deleteFriend(requesterId = currentUserId, acceptorId = targetUserId)
            return@runCatching response.getDataOrThrowMessage()
        }
    }
}
