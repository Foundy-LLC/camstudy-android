package io.foundy.ranking.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.ranking.data.model.UserRankingOverviewDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RankingApi {

    @GET("ranking")
    fun getUserRankingList(
        @Query("organizationId") organizationId: String? = null,
        @Query("page") page: Int = 0,
        @Query("weekly") isWeekly: Boolean = false
    ): CamstudyResponse<List<UserRankingOverviewDto>>

    @GET("ranking/{userId}")
    fun getUserRanking(
        @Path("userId") userId: String,
        @Query("weekly") isWeekly: Boolean = false,
    ): CamstudyResponse<UserRankingOverviewDto>
}
