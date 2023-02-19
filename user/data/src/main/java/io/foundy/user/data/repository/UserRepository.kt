package io.foundy.user.data.repository

interface UserRepository {
    suspend fun getUserExistence(userId: String): Result<Boolean>
}
