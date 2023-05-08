package io.foundy.user.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.data.model.FriendStatusDto
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop
import io.foundy.core.model.User

data class UserDto(
    val id: String,
    val name: String,
    val introduce: String?,
    val profileImage: String?,
    @SerializedName("requestHistory") val friendStatus: FriendStatusDto,
    val organizations: List<String>,
    val tags: List<String>
)

fun UserDto.toEntity(
    weeklyRanking: Int,
    totalRanking: Int,
    weeklyStudyTimeSec: Int,
    weeklyStudyTimeOverall: Int,
    growingCrop: GrowingCrop?,
    harvestedCrops: List<HarvestedCrop>,
): User = User(
    id = id,
    name = name,
    introduce = introduce,
    profileImage = profileImage,
    weeklyRanking = weeklyRanking,
    totalRanking = totalRanking,
    weeklyStudyTimeSec = weeklyStudyTimeSec,
    weeklyStudyTimeOverall = weeklyStudyTimeOverall,
    growingCrop = growingCrop,
    harvestedCrops = harvestedCrops,
    organizations = organizations,
    tags = tags
)
