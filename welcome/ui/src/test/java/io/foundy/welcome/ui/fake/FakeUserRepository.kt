package io.foundy.welcome.ui.fake

import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.FriendStatus
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop
import io.foundy.core.model.User
import io.foundy.user.domain.repository.UserRepository
import java.io.File
import java.util.Date

class FakeUserRepository : UserRepository {

    override suspend fun getUser(id: String): Result<User> {
        return Result.success(
            User(
                id = "id",
                isMe = false,
                name = "김민성",
                introduce = "안녕하세요",
                profileImage = null,
                weeklyRanking = 23,
                totalRanking = 40,
                weeklyStudyTimeSec = 23142,
                weeklyRankingOverall = 42,
                growingCrop = GrowingCrop(
                    id = "gid",
                    ownerId = "id",
                    type = CropType.PUMPKIN,
                    level = 2,
                    expectedGrade = CropGrade.GOLD,
                    isDead = false,
                    plantedAt = Date()
                ),
                harvestedCrops = listOf(
                    HarvestedCrop(
                        type = CropType.TOMATO,
                        grade = CropGrade.SILVER,
                        plantedAt = Date(),
                        harvestedAt = Date()
                    )
                ),
                organizations = listOf("한성대학교"),
                tags = listOf("안드로이드", "개발", "웹"),
                consecutiveStudyDays = 4,
                friendStatus = FriendStatus.NONE
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
