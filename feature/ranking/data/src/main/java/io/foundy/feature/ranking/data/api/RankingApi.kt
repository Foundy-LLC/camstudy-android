package io.foundy.feature.ranking.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.feature.ranking.data.model.UserRankingListResponse
import io.foundy.feature.ranking.data.model.UserRankingResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RankingApi {

    @GET("ranking")
    suspend fun getUserRankingList(
        @Query("organizationId") organizationId: String? = null,
        @Query("page") page: Int = 0,
        @Query("weekly") isWeekly: Boolean = false
    ): CamstudyResponse<UserRankingListResponse>

    @GET("ranking/{userId}")
    suspend fun getUserRanking(
        @Path("userId") userId: String,
        @Query("weekly") isWeekly: Boolean = false,
        @Query("organizationId") organizationId: String? = null
    ): CamstudyResponse<UserRankingResponse>
}
