package io.foundy.feature.friend.data.model

data class GetFriendsResponse(
    val maxPage: Int,
    val friends: List<UserOverviewDto>
)
