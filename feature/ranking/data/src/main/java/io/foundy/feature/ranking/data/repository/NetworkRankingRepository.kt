package io.foundy.feature.ranking.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.UserRankingOverview
import io.foundy.feature.ranking.data.api.RankingApi
import io.foundy.feature.ranking.data.model.toEntity
import io.foundy.feature.ranking.data.source.RankingPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkRankingRepository @Inject constructor(
    private val rankingApi: RankingApi
) : RankingRepository {

    override fun getUserRankingList(
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

    override suspend fun getUserRanking(
        userId: String,
        isWeekly: Boolean,
        organizationId: String?
    ): Result<UserRankingOverview> {
        return runCatching {
            val response = rankingApi.getUserRanking(
                userId = userId,
                isWeekly = isWeekly,
                organizationId = organizationId
            )
            response.getDataOrThrowMessage().user.toEntity()
        }
    }
}
