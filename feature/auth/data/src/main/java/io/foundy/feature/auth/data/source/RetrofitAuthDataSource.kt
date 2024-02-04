package io.foundy.feature.auth.data.source

import io.foundy.core.data.util.CamstudyApiResponse
import io.foundy.feature.auth.data.api.AuthApi
import javax.inject.Inject

class RetrofitAuthDataSource @Inject constructor(
    private val authApi: AuthApi
) : AuthRemoteDataSource {

    override suspend fun getUserInitialInfoExistence(
        userId: String
    ): CamstudyApiResponse<Boolean> {
        return authApi.getUserInitialInfoExistence(userId)
    }
}
