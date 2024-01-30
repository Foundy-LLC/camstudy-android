package io.foundy.feature.auth.data.source

import io.foundy.core.data.model.ResponseBody
import retrofit2.Response

interface AuthRemoteDataSource {
    suspend fun getUserInitialInfoExistence(userId: String): Response<ResponseBody<Boolean>>
}
