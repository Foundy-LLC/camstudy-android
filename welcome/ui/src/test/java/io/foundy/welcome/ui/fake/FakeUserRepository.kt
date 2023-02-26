package io.foundy.welcome.ui.fake

import io.foundy.core.model.User
import io.foundy.user.domain.repository.UserRepository
import java.io.File

class FakeUserRepository : UserRepository {

    override suspend fun getUser(id: String): Result<User> {
        return Result.success(
            User(
                id = "id",
                name = "name",
                introduce = "hello",
                rankingScore = 100,
                totalStudyMinute = 60,
                organizations = emptyList(),
                tags = listOf("android")
            )
        )
    }

    override suspend fun postUserInitialInfo(
        userId: String,
        name: String,
        introduce: String?,
        tags: List<String>,
        profileImage: File?
    ): Result<Unit> {
        return Result.success(Unit)
    }
}
