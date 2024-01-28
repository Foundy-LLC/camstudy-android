package io.foundy.welcome.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.welcome.data.model.TagDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WelcomeApi {

    @GET("tags")
    suspend fun getTags(
        @Query("page") page: Int,
        @Query("name") name: String
    ): CamstudyResponse<List<TagDto>>
}
