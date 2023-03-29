package io.foundy.auth.data.api

import io.foundy.core.data.util.CamstudyResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface AuthApi {

    @GET("users/{userId}/exists")
    suspend fun getUserInitialInfoExistence(
        @Path("userId") userId: String
    ): CamstudyResponse<Boolean>
}
