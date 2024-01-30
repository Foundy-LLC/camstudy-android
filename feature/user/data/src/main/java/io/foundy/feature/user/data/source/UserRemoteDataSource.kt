package io.foundy.feature.user.data.source

import io.foundy.core.data.model.ResponseBody
import io.foundy.core.data.util.CamstudyResponse
import io.foundy.feature.user.data.model.UserCreateRequestBody
import io.foundy.feature.user.data.model.UserDto
import io.foundy.feature.user.data.model.UserUpdateRequestBody
import okhttp3.MultipartBody
import retrofit2.Response

interface UserRemoteDataSource {

    suspend fun getUser(userId: String, requesterId: String): Response<ResponseBody<UserDto>>

    suspend fun postUserInitialInfo(body: UserCreateRequestBody): Response<ResponseBody<Unit>>

    suspend fun uploadUserProfileImage(
        userId: String,
        multipartBody: MultipartBody.Part
    ): Response<ResponseBody<String>>

    suspend fun updateUserProfile(
        userId: String,
        body: UserUpdateRequestBody
    ): CamstudyResponse<Unit>

    suspend fun removeProfileImage(
        userId: String
    ): CamstudyResponse<Unit>
}
