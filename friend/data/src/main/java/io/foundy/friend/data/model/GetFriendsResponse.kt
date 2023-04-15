package io.foundy.friend.data.model

data class GetFriendsResponse(
    val maxPage: Int,
    val friends: List<UserOverviewDto>
)
