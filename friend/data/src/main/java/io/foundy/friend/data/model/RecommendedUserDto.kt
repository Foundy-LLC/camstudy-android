package io.foundy.friend.data.model

import io.foundy.core.data.model.FriendStatusDto
import io.foundy.core.data.model.toEntity
import io.foundy.core.model.RecommendedUser

data class RecommendedUserDto(
    val id: String,
    val name: String,
    val profileImage: String?,
    val introduce: String?,
    val requestHistory: FriendStatusDto
)

fun RecommendedUserDto.toEntity() = RecommendedUser(
    id = id,
    name = name,
    profileImage = profileImage,
    introduce = introduce,
    friendStatus = requestHistory.toEntity()
)
