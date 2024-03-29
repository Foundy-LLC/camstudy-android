package io.foundy.feature.friend.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.UserOverview
import io.foundy.feature.friend.data.api.FriendApi
import io.foundy.feature.friend.data.model.toEntity
import javax.inject.Inject

class FriendPagingSource @Inject constructor(
    private val api: FriendApi,
    private val userId: String,
    private val accepted: Boolean
) : PagingSource<Int, UserOverview>() {

    companion object {
        private const val START_PAGE = 0
        const val PAGE_SIZE = 10
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserOverview> {
        val page = params.key ?: START_PAGE
        runCatching {
            if (accepted) {
                api.getFriends(page = page, userId = userId).getDataOrThrowMessage().friends
            } else {
                api.getFriendRequests(page = page, userId = userId).getDataOrThrowMessage()
            }
        }.onSuccess {
            val roomOverviews = it.map { dto -> dto.toEntity() }
            val isEnd = roomOverviews.size < PAGE_SIZE

            return LoadResult.Page(
                data = roomOverviews,
                prevKey = if (page == START_PAGE) null else page - 1,
                nextKey = if (isEnd) null else page + 1
            )
        }.onFailure {
            return LoadResult.Error(it)
        }
        return LoadResult.Invalid()
    }

    override fun getRefreshKey(state: PagingState<Int, UserOverview>): Int? {
        return null
    }
}
