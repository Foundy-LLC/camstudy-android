package io.foundy.core.model

data class SearchedUser(
    val id: String,
    val name: String,
    val introduce: String?,
    val profileImage: String?,
    val friendStatus: FriendStatus
)
