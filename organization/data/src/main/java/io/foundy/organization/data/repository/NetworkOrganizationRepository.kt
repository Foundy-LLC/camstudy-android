package io.foundy.organization.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.Organization
import io.foundy.core.model.OrganizationOverview
import io.foundy.organization.data.api.OrganizationApi
import io.foundy.organization.data.source.OrganizationPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkOrganizationRepository @Inject constructor(
    private val api: OrganizationApi
) : OrganizationRepository {

    override fun getOrganizations(name: String): Flow<PagingData<Organization>> {
        return Pager(
            config = PagingConfig(OrganizationPagingSource.PAGE_SIZE),
            pagingSourceFactory = {
                OrganizationPagingSource(api = api, nameQuery = name)
            }
        ).flow
    }

    override suspend fun getUserOrganizations(userId: String): Result<List<OrganizationOverview>> {
        return runCatching {
            val response = api.getUserOrganizations(userId = userId)
            response.getDataOrThrowMessage()
        }
    }
}
