package io.foundy.ranking.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.UserRankingOverview
import io.foundy.ranking.data.api.RankingApi
import io.foundy.ranking.data.model.toEntity
import io.foundy.ranking.data.source.RankingPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkRankingRepository @Inject constructor(
    private val rankingApi: RankingApi
) : RankingRepository {

    override fun getUserRankingStream(
        organizationId: String?,
        isWeekly: Boolean
    ): Flow<PagingData<UserRankingOverview>> {
        return Pager(
            config = PagingConfig(pageSize = RankingPagingSource.PAGE_SIZE),
            pagingSourceFactory = {
                RankingPagingSource(
                    api = rankingApi,
                    organizationId = organizationId,
                    isWeekly = isWeekly
                )
            }
        ).flow
    }

    override fun getUserRanking(userId: String, isWeekly: Boolean): Result<UserRankingOverview> {
        return runCatching {
            val response = rankingApi.getUserRanking(userId = userId, isWeekly = isWeekly)
            response.getDataOrThrowMessage().toEntity()
        }
    }
}
