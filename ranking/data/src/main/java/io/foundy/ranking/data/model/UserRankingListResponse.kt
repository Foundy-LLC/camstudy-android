package io.foundy.ranking.data.model

data class UserRankingListResponse(
    val totalUserCount: Int,
    val users: List<UserRankingOverviewDto>
)
