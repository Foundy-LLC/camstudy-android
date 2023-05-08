package io.foundy.core.model

data class User(
    val id: String,
    val name: String,
    val introduce: String?,
    val profileImage: String?,
    val weeklyRanking: Int,
    val totalRanking: Int,
    val weeklyStudyTimeSec: Int,
    val weeklyStudyTimeOverall: Int,
    val growingCrop: GrowingCrop?,
    val harvestedCrops: List<HarvestedCrop>,
    val organizations: List<String>,
    val tags: List<String>
)
