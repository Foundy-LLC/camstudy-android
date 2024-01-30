package io.foundy.feature.user.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.data.model.FriendStatusDto
import io.foundy.core.data.model.toEntity
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop
import io.foundy.core.model.User

data class UserDto(
    val id: String,
    val name: String,
    val introduce: String?,
    val consecutiveStudyDays: Int,
    val profileImage: String?,
    @SerializedName("requestHistory") val friendStatus: FriendStatusDto,
    val organizations: List<String>,
    val tags: List<String>
)

fun UserDto.toEntity(
    isMe: Boolean,
    weeklyRanking: Int,
    weeklyRankingScore: Int,
    weeklyStudyTimeSec: Int,
    weeklyRankingOverall: Int,
    growingCrop: GrowingCrop?,
    harvestedCrops: List<HarvestedCrop>,
): User = User(
    id = id,
    isMe = isMe,
    name = name,
    introduce = introduce,
    hasWeeklyRanking = weeklyRankingScore != 0,
    profileImage = profileImage,
    consecutiveStudyDays = consecutiveStudyDays,
    weeklyRankingOverall = weeklyRankingOverall,
    weeklyRanking = weeklyRanking,
    weeklyStudyTimeSec = weeklyStudyTimeSec,
    growingCrop = growingCrop,
    harvestedCrops = harvestedCrops,
    organizations = organizations,
    tags = tags,
    friendStatus = friendStatus.toEntity()
)
