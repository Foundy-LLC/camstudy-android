package io.foundy.ranking.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.UserRankingOverview
import io.foundy.ranking.data.api.RankingApi
import io.foundy.ranking.data.model.toEntity

class RankingPagingSource(
    private val api: RankingApi,
    private val organizationId: String? = null,
    private val isWeekly: Boolean = false
) : PagingSource<Int, UserRankingOverview>() {

    companion object {
        private const val START_PAGE = 0
        const val PAGE_SIZE = 50
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserRankingOverview> {
        val page = params.key ?: START_PAGE
        runCatching {
            val response = api.getUserRanking(
                page = page,
                organizationId = organizationId,
                isWeekly = isWeekly
            )
            response.getDataOrThrowMessage()
        }.onSuccess {
            val users = it.map { dto -> dto.toEntity() }
            val isEnd = users.size < PAGE_SIZE

            return LoadResult.Page(
                data = users,
                prevKey = if (page == START_PAGE) null else page - 1,
                nextKey = if (isEnd) null else page + 1
            )
        }.onFailure {
            return LoadResult.Error(it)
        }
        return LoadResult.Invalid()
    }

    override fun getRefreshKey(state: PagingState<Int, UserRankingOverview>): Int? = null
}
