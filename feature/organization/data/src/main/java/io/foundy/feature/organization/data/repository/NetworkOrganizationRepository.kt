package io.foundy.feature.organization.data.repository

import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.Organization
import io.foundy.core.model.OrganizationOverview
import io.foundy.feature.organization.data.api.OrganizationApi
import io.foundy.feature.organization.data.model.OrganizationAddingRequestBody
import io.foundy.feature.organization.data.model.toEntity
import javax.inject.Inject

class NetworkOrganizationRepository @Inject constructor(
    private val api: OrganizationApi
) : OrganizationRepository {

    override suspend fun getOrganizations(name: String): Result<List<Organization>> {
        return runCatching {
            val response = api.getOrganizations(name = name, page = 0)
            response.getDataOrThrowMessage().map { it.toEntity() }
        }
    }

    override suspend fun getUserOrganizations(userId: String): Result<List<OrganizationOverview>> {
        return runCatching {
            val response = api.getUserOrganizations(userId = userId)
            response.getDataOrThrowMessage().map { it.toEntity() }
        }
    }

    override suspend fun requestOrganizationAdding(
        userId: String,
        organizationId: String,
        email: String
    ): Result<Unit> {
        return runCatching {
            val requestBody = OrganizationAddingRequestBody(
                email = email,
                organizationId = organizationId
            )
            val response = api.requestOrganizationAdding(
                userId = userId,
                body = requestBody
            )
            response.getDataOrThrowMessage()
        }
    }

    override suspend fun removeOrganization(userId: String, organizationId: String): Result<Unit> {
        return runCatching {
            val response = api.deleteOrganization(userId = userId, organizationId = organizationId)
            response.getDataOrThrowMessage()
        }
    }
}
