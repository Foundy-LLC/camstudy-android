package io.foundy.friend.data.model

data class GetRecommendedFriendsResponse(
    val totalUserCount: Int,
    val users: List<UserOverviewDto>
)
