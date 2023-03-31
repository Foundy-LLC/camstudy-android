package io.foundy.organization.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.Organization
import io.foundy.organization.data.api.OrganizationApi
import io.foundy.organization.data.model.toEntity

class OrganizationPagingSource(
    private val api: OrganizationApi,
    private val nameQuery: String
) : PagingSource<Int, Organization>() {

    companion object {
        private const val START_PAGE = 0
        const val PAGE_SIZE = 20
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Organization> {
        val page = params.key ?: START_PAGE
        runCatching {
            val response = api.getOrganizations(page = page, name = nameQuery)
            response.getDataOrThrowMessage()
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

    override fun getRefreshKey(state: PagingState<Int, Organization>): Int? {
        return null
    }
}
