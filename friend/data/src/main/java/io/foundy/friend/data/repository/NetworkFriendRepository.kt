package io.foundy.friend.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.foundy.core.model.UserOverview
import io.foundy.friend.data.api.FriendApi
import io.foundy.friend.data.source.FriendPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkFriendRepository @Inject constructor(
    private val api: FriendApi
) : FriendRepository {

    override suspend fun getFriends(userId: String): Flow<PagingData<UserOverview>> {
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

    override suspend fun getFriendRequests(userId: String): Flow<PagingData<UserOverview>> {
        TODO("Not yet implemented")
    }
}
