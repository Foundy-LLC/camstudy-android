package io.foundy.core.model

data class User(
    val id: String,
    val name: String,
    val introduce: String?,
    val profileImage: String?,
    val consecutiveStudyDays: Int,
    val hasWeeklyRanking: Boolean,
    val weeklyRanking: Int,
    val weeklyRankingOverall: Int,
    val weeklyStudyTimeSec: Int,
    val growingCrop: GrowingCrop?,
    val harvestedCrops: List<HarvestedCrop>,
    val organizations: List<String>,
    val tags: List<String>,
    val friendStatus: FriendStatus,
    val isMe: Boolean
)
