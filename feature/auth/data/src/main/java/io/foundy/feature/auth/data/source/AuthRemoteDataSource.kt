package io.foundy.feature.auth.data.source

import io.foundy.core.data.util.CamstudyApiResponse

interface AuthRemoteDataSource {
    suspend fun getUserInitialInfoExistence(userId: String): CamstudyApiResponse<Boolean>
}
