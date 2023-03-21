package io.foundy.welcome.data.api

import io.foundy.core.data.model.ResponseBody
import io.foundy.welcome.data.model.TagDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WelcomeApi {

    @GET("tags")
    suspend fun getTags(
        @Query("page") page: Int,
        @Query("name") name: String
    ): Response<ResponseBody<List<TagDto>>>
}
