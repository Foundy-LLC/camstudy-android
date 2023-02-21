package io.foundy.room_list.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.RoomOverview
import io.foundy.room_list.data.api.RoomListApi
import io.foundy.room_list.data.model.toEntity
import javax.inject.Inject

class RoomPagingSource @Inject constructor(
    private val api: RoomListApi
) : PagingSource<Int, RoomOverview>() {

    companion object {
        private const val START_PAGE = 0
        const val PAGE_SIZE = 20
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RoomOverview> {
        val page = params.key ?: START_PAGE
        runCatching {
            api.getRooms(page = page).getDataOrThrowMessage()
        }.onSuccess {
            val roomOverviews = it.map { dto -> dto.toEntity() }
            val isEnd = roomOverviews.isEmpty()

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

    override fun getRefreshKey(state: PagingState<Int, RoomOverview>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
