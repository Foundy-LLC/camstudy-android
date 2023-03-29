package io.foundy.search.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.search.data.model.SearchedUserDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("users")
    suspend fun searchUsers(
        @Query("name") name: String = "",
        @Query("id") exceptUserId: String
    ): CamstudyResponse<List<SearchedUserDto>>
}
