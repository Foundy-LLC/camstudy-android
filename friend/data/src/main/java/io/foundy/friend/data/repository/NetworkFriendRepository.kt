package io.foundy.friend.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.UserOverview
import io.foundy.friend.data.api.FriendApi
import io.foundy.friend.data.model.FriendPostRequestBody
import io.foundy.friend.data.source.FriendPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class NetworkFriendRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val api: FriendApi
) : FriendRepository {

    private suspend fun requireCurrentUserId(): String {
        val currentUserId = authRepository.currentUserIdStream.firstOrNull()
        check(currentUserId != null)
        return currentUserId
    }

    override fun getFriends(userId: String): Flow<PagingData<UserOverview>> {
        return Pager(
            config = PagingConfig(FriendPagingSource.PAGE_SIZE),
            pagingSourceFactory = {
                FriendPagingSource(
                    api = api,
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
                    api = api,
                    userId = userId,
                    accepted = false
                )
            }
        ).flow
    }

    override suspend fun requestFriend(targetUserId: String): Result<Unit> {
        val currentUserId = requireCurrentUserId()
        return runCatching {
            val response = api.requestFriend(
                requesterId = currentUserId,
                body = FriendPostRequestBody(targetUserId = targetUserId)
            )
            return@runCatching response.getDataOrThrowMessage()
        }
    }

    override suspend fun acceptFriendRequest(requesterId: String): Result<Unit> {
        val currentUserId = requireCurrentUserId()
        return runCatching {
            val response = api.acceptRequest(userId = currentUserId, friendId = requesterId)
            return@runCatching response.getDataOrThrowMessage()
        }
    }

    override suspend fun rejectFriendRequest(requesterId: String): Result<Unit> {
        val currentUserId = requireCurrentUserId()
        return runCatching {
            val response = api.deleteFriend(requesterId = requesterId, acceptorId = currentUserId)
            return@runCatching response.getDataOrThrowMessage()
        }
    }

    override suspend fun deleteFriend(targetUserId: String): Result<Unit> {
        val currentUserId = requireCurrentUserId()
        return runCatching {
            val response = api.deleteFriend(requesterId = currentUserId, acceptorId = targetUserId)
            return@runCatching response.getDataOrThrowMessage()
        }
    }
}
