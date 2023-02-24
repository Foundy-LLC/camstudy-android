package io.foundy.user.data.model

import io.foundy.core.model.User

data class UserDto(
    val id: String,
    val name: String,
    val introduce: String?,
    val rankingScore: Float,
    val totalStudyMinute: Float,
    val organizations: List<String>,
    val tags: List<String>
)

fun UserDto.toEntity(): User = User(
    id = id,
    name = name,
    introduce = introduce,
    rankingScore = rankingScore.toInt(),
    totalStudyMinute = totalStudyMinute.toInt(),
    organizations = organizations,
    tags = tags
)
