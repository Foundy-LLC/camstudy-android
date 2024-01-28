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

    /**
     * 회원 프로필을 업데이트한다.
     *
     * @return 새로 업로드된 회원 프로필의 이미지 URL을 반환한다. 없다면 null을 반환한다.
     */
    suspend fun updateUserProfile(
        name: String,
        introduce: String?,
        tags: List<String>,
        profileImage: File?,
        shouldRemoveProfileImage: Boolean
    ): Result<String?>
}
