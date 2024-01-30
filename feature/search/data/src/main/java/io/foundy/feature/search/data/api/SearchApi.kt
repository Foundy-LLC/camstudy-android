package io.foundy.feature.search.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.feature.search.data.model.SearchUsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("users")
    suspend fun searchUsers(
        @Query("name") name: String = "",
        @Query("id") exceptUserId: String
    ): CamstudyResponse<SearchUsersResponse>
}
