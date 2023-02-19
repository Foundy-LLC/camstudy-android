package io.foundy.core.model

data class User(
    val id: String,
    val name: String,
    val introduce: String?,
    val rankingScore: Int,
    val totalStudyMinute: Int,
    val organizations: List<String>,
    val tags: List<String>
)
