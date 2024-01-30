package io.foundy.feature.user.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.feature.user.data.model.UserCreateRequestBody
import io.foundy.feature.user.data.model.UserDto
import io.foundy.feature.user.data.model.UserUpdateRequestBody
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: String,
        @Query("requesterId") requesterId: String
    ): CamstudyResponse<UserDto>

    @GET("users/{userId}/exists")
    suspend fun getUserExistence(
        @Path("userId") userId: String
    ): CamstudyResponse<Boolean>

    @POST("users")
    suspend fun postUserInitialInfo(
        @Body body: UserCreateRequestBody
    ): CamstudyResponse<Unit>

    @Multipart
    @POST("users/{userId}/profile-image")
    suspend fun uploadUserProfileImage(
        @Path("userId") userId: String,
        @Part profileImage: MultipartBody.Part
    ): CamstudyResponse<String>

    @PATCH("users/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: String,
        @Body body: UserUpdateRequestBody
    ): CamstudyResponse<Unit>

    @DELETE("users/{userId}/profile-image")
    suspend fun removeUserProfileImage(
        @Path("userId") userId: String,
    ): CamstudyResponse<Unit>
}
