package io.foundy.organization.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.organization.data.model.OrganizationDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OrganizationApi {

    @GET("organizations")
    suspend fun getOrganizations(
        @Query("name") name: String,
        @Query("page") page: Int
    ): CamstudyResponse<List<OrganizationDto>>
}
