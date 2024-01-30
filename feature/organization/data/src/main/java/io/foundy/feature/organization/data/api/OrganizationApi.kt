package io.foundy.feature.organization.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.feature.organization.data.model.OrganizationAddingRequestBody
import io.foundy.feature.organization.data.model.OrganizationDto
import io.foundy.feature.organization.data.model.OrganizationOverviewDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrganizationApi {

    @GET("organizations")
    suspend fun getOrganizations(
        @Query("name") name: String,
        @Query("page") page: Int
    ): CamstudyResponse<List<OrganizationDto>>

    @GET("users/{userId}/organizations")
    suspend fun getUserOrganizations(
        @Path("userId") userId: String
    ): CamstudyResponse<List<OrganizationOverviewDto>>

    @POST("users/{userId}/organizations")
    suspend fun requestOrganizationAdding(
        @Path("userId") userId: String,
        @Body body: OrganizationAddingRequestBody
    ): CamstudyResponse<Unit>

    @DELETE("users/{userId}/organizations/{organizationId}")
    suspend fun deleteOrganization(
        @Path("userId") userId: String,
        @Path("organizationId") organizationId: String
    ): CamstudyResponse<Unit>
}
