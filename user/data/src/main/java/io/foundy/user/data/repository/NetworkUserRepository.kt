package io.foundy.user.data.repository

import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.user.data.source.UserRemoteDataSource
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(
    private val userDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun getUserExistence(userId: String): Result<Boolean> {
        return runCatching {
            val response = userDataSource.getUserExistence(userId)
            response.getDataOrThrowMessage()
        }
    }
}
