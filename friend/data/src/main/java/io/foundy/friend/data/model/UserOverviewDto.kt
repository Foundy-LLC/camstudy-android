package io.foundy.friend.data.model

import io.foundy.core.model.UserOverview

data class UserOverviewDto(
    val id: String,
    val name: String,
    val profileImage: String?,
    val rankingScore: Int
)

fun UserOverviewDto.toEntity() = UserOverview(
    id = id,
    name = name,
    profileImage = profileImage,
    rankingScore = rankingScore
)
