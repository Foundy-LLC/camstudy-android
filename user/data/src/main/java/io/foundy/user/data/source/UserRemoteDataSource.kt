package io.foundy.user.data.source

import io.foundy.core.data.model.ResponseBody
import io.foundy.user.data.model.UserCreateRequestBody
import io.foundy.user.data.model.UserDto
import okhttp3.MultipartBody
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun getUser(userId: String, requesterId: String): Response<ResponseBody<UserDto>>
    suspend fun postUserInitialInfo(body: UserCreateRequestBody): Response<ResponseBody<Unit>>
    suspend fun uploadUserProfileImage(
        userId: String,
        multipartBody: MultipartBody.Part
    ): Response<ResponseBody<String>>
}
