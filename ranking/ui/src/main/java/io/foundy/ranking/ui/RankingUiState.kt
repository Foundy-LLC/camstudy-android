package io.foundy.ranking.ui

import androidx.paging.PagingData
import io.foundy.core.model.OrganizationOverview
import io.foundy.core.model.UserRankingOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class RankingUiState(
    val totalRanking: RankingTabUiState,
    val weeklyRanking: RankingTabUiState,
    val organizations: List<OrganizationOverview> = emptyList(),
    val selectedOrganization: OrganizationOverview? = null,
    val onSelectOrganization: (OrganizationOverview?) -> Unit
) {
    fun getCurrentTabUiStateBy(tab: RankingTabDestination): RankingTabUiState {
        return when (tab) {
            RankingTabDestination.Total -> totalRanking
            RankingTabDestination.Weekly -> weeklyRanking
        }
    }
}

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
