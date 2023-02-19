package io.foundy.user.data.source

import io.foundy.core.data.model.ResponseBody
import io.foundy.user.data.api.UserApi
import io.foundy.user.data.model.UserDto
import retrofit2.Response
import javax.inject.Inject

class RetrofitUserDataSource @Inject constructor(
    private val api: UserApi
) : UserRemoteDataSource {

    override suspend fun getUser(userId: String): Response<ResponseBody<UserDto>> {
        return api.getUser(userId)
    }

    override suspend fun getUserExistence(userId: String): Response<ResponseBody<Boolean>> {
        return api.getUserExistence(userId)
    }
}
