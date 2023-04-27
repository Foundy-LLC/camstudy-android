package io.foundy.ranking.data.repository

import androidx.paging.PagingData
import io.foundy.core.model.UserRankingOverview
import kotlinx.coroutines.flow.Flow

interface RankingRepository {

    fun getUserRankingList(
        organizationId: String?,
        isWeekly: Boolean
    ): Flow<PagingData<UserRankingOverview>>

    fun getUserRanking(
        userId: String,
        isWeekly: Boolean
    ): Result<UserRankingOverview>
}
