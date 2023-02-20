package io.foundy.user.data.source

import io.foundy.core.data.model.ResponseBody
import io.foundy.user.data.api.UserApi
import io.foundy.user.data.model.UserCreateRequestBody
import io.foundy.user.data.model.UserDto
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class RetrofitUserDataSource @Inject constructor(
    private val api: UserApi
) : UserRemoteDataSource {

    override suspend fun getUser(userId: String): Response<ResponseBody<UserDto>> {
        return api.getUser(userId)
    }

    override suspend fun postUserInitialInfo(
        body: UserCreateRequestBody
    ): Response<ResponseBody<Unit>> {
        return api.postUserInitialInfo(body = body)
    }

    override suspend fun uploadUserProfileImage(
        userId: String,
        multipartBody: MultipartBody.Part
    ): Response<ResponseBody<String>> {
        return api.uploadUserProfileImage(userId = userId, profileImage = multipartBody)
    }
}
