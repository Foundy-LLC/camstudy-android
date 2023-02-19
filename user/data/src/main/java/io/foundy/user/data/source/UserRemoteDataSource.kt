package io.foundy.user.data.source

import io.foundy.core.data.model.ResponseBody
import io.foundy.user.data.model.UserDto
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun getUser(userId: String): Response<ResponseBody<UserDto>>
    suspend fun getUserExistence(userId: String): Response<ResponseBody<Boolean>>
}
