package io.foundy.auth.data.source

import io.foundy.auth.data.api.AuthApi
import io.foundy.core.data.model.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class RetrofitAuthDataSource @Inject constructor(
    private val authApi: AuthApi
) : AuthRemoteDataSource {

    override suspend fun getUserInitialInfoExistence(
        userId: String
    ): Response<ResponseBody<Boolean>> {
        return authApi.getUserInitialInfoExistence(userId)
    }
}
