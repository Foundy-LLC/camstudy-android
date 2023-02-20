package io.foundy.auth.data.api

import io.foundy.core.data.model.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AuthApi {

    @GET("users/{userId}/exists")
    suspend fun getUserInitialInfoExistence(
        @Path("userId") userId: String
    ): Response<ResponseBody<Boolean>>
}
