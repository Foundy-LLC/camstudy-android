package io.foundy.ranking.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.model.UserRankingOverview

data class UserRankingOverviewDto(
    val id: String,
    val name: String,
    val profileImage: String?,
    @SerializedName("rankingScore") val score: Int,
    val ranking: Int
)

fun UserRankingOverviewDto.toEntity() = UserRankingOverview(
    id = id,
    name = name,
    profileImage = profileImage,
    score = score,
    ranking = ranking
)
