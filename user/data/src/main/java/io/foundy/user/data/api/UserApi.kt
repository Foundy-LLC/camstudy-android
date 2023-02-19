package io.foundy.user.data.api

import io.foundy.core.data.model.ResponseBody
import io.foundy.user.data.model.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: String
    ): Response<ResponseBody<UserDto>>

    @GET("users/{userId}/exists")
    suspend fun getUserExistence(
        @Path("userId") userId: String
    ): Response<ResponseBody<Boolean>>
}
