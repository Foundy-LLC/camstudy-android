package io.foundy.feature.ranking.data.model

import com.google.gson.annotations.SerializedName

data class UserRankingResponse(
    val totalUserCount: Int,
    @SerializedName("users") val user: UserRankingOverviewDto
)
