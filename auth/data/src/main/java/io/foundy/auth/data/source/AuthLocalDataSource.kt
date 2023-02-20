package io.foundy.auth.data.source

interface AuthLocalDataSource {
    suspend fun markAsUserInitialInfoExists(userId: String)
    suspend fun existsUserInitialInfo(userId: String): Boolean
}
