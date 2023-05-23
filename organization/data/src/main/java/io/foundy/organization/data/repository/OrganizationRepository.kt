package io.foundy.organization.data.repository

import androidx.paging.PagingData
import io.foundy.core.model.Organization
import io.foundy.core.model.OrganizationOverview
import kotlinx.coroutines.flow.Flow

interface OrganizationRepository {
    fun getOrganizations(name: String): Flow<PagingData<Organization>>
    suspend fun getUserOrganizations(userId: String): Result<List<OrganizationOverview>>
    suspend fun removeOrganization(userId: String, organizationId: String): Result<Unit>
}
