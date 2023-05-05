package io.foundy.ranking.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.model.UserRankingOverview

data class UserRankingOverviewDto(
    val id: String,
    val name: String,
    val profileImage: String?,
    val introduce: String,
    @SerializedName("rankingScore") val score: Int,
    val ranking: Int,
    @SerializedName("studyTime") val studyTimeSec: Int
)

fun UserRankingOverviewDto.toEntity() = UserRankingOverview(
    id = id,
    name = name,
    profileImage = profileImage,
    introduce = introduce,
    score = score,
    ranking = ranking,
    studyTimeSec = studyTimeSec
)
