package io.foundy.core.model

data class UserRankingOverview(
    val id: String,
    val name: String,
    val profileImage: String?,
    val score: Int,
    val ranking: Int
)
