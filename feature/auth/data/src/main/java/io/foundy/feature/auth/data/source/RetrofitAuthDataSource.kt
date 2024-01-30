package io.foundy.feature.auth.data.source

import io.foundy.core.data.model.ResponseBody
import io.foundy.feature.auth.data.api.AuthApi
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
