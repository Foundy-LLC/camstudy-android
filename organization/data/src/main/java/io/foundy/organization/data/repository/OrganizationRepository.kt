package io.foundy.organization.data.repository

import io.foundy.core.model.Organization
import io.foundy.core.model.OrganizationOverview

interface OrganizationRepository {
    suspend fun getOrganizations(name: String): Result<List<Organization>>
    suspend fun getUserOrganizations(userId: String): Result<List<OrganizationOverview>>
    suspend fun removeOrganization(userId: String, organizationId: String): Result<Unit>
}
