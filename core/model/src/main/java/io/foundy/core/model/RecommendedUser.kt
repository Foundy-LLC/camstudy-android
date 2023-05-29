package io.foundy.core.model

data class RecommendedUser(
    val id: String,
    val name: String,
    val profileImage: String?,
    val introduce: String?,
    val friendStatus: FriendStatus
)
