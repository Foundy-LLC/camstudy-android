package io.foundy.organization.data.repository

import androidx.paging.PagingData
import io.foundy.core.model.Organization
import kotlinx.coroutines.flow.Flow

interface OrganizationRepository {
    fun getOrganizations(name: String): Flow<PagingData<Organization>>
}
