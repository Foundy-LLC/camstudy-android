package io.foundy.user.domain.repository

import io.foundy.core.model.User
import java.io.File

interface UserRepository {

    suspend fun getUser(id: String): Result<User>

    suspend fun postUserInitialInfo(
        userId: String,
        name: String,
        introduce: String?,
        tags: List<String>,
        profileImage: File?
    ): Result<Unit>

    suspend fun updateUserProfile(
        name: String,
        introduce: String?,
        tags: List<String>,
        profileImage: File?,
        shouldRemoveProfileImage: Boolean
    ): Result<Unit>
}
