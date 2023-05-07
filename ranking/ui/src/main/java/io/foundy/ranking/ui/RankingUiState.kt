package io.foundy.ranking.ui

import androidx.paging.PagingData
import io.foundy.core.model.UserRankingOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class RankingUiState(
    val totalRanking: RankingTabUiState,
    val weeklyRanking: RankingTabUiState,
)

data class RankingTabUiState(
    val currentUserRanking: UserRankingOverview? = null,
    val rankingFlow: Flow<PagingData<UserRankingOverview>> = emptyFlow(),
    val isCurrentUserRankingLoading: Boolean = false,
    val fetchCurrentUserRanking: () -> Unit
) {
    val shouldFetchCurrentUserRanking: Boolean
        get() = currentUserRanking == null &&
            !isCurrentUserRankingLoading
}
