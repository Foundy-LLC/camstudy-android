package io.foundy.core.model

data class UserOverview(
    val id: String,
    val name: String,
    val profileImage: String?,
    val rankingScore: Int
)
