package io.foundy.user.data.repository

import java.io.File

interface UserRepository {
    suspend fun postUserInitialInfo(
        userId: String,
        name: String,
        introduce: String?,
        tags: List<String>,
        profileImage: File?
    ): Result<Unit>
}
