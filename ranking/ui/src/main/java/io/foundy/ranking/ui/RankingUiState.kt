package io.foundy.ranking.ui

import androidx.paging.PagingData
import io.foundy.core.model.UserRankingOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class RankingUiState(
    // TODO: 내 랭킹 추가
    val totalUserRankingFlow: Flow<PagingData<UserRankingOverview>> = emptyFlow(),
    val weeklyUserRankingFlow: Flow<PagingData<UserRankingOverview>> = emptyFlow(),
    // TODO: 소속별 랭킹 추가
)
