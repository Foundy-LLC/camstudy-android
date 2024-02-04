package io.foundy.feature.auth.data.api

import io.foundy.core.data.util.CamstudyApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface AuthApi {

    @GET("users/{userId}/exists")
    suspend fun getUserInitialInfoExistence(
        @Path("userId") userId: String
    ): CamstudyApiResponse<Boolean>
}
