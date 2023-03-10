package io.foundy.user.data.api

import io.foundy.core.data.model.ResponseBody
import io.foundy.user.data.model.UserCreateRequestBody
import io.foundy.user.data.model.UserDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @POST("users")
    suspend fun postUserInitialInfo(
        @Body body: UserCreateRequestBody
    ): Response<ResponseBody<Unit>>

    @Multipart
    @POST("users/{userId}/profile-image")
    suspend fun uploadUserProfileImage(
        @Path("userId") userId: String,
        @Part profileImage: MultipartBody.Part
    ): Response<ResponseBody<String>>
}
