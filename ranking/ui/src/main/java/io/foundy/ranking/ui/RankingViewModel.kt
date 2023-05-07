package io.foundy.ranking.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.ranking.data.repository.RankingRepository
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val rankingRepository: RankingRepository
) : ViewModel(), ContainerHost<RankingUiState, RankingSideEffect> {

    override val container: Container<RankingUiState, RankingSideEffect> =
        container(RankingUiState())

    init {
        intent {
            reduce {
                state.copy(
                    totalUserRankingFlow = rankingRepository.getUserRankingList(
                        organizationId = null,
                        isWeekly = false
                    ).cachedIn(viewModelScope),
                    weeklyUserRankingFlow = rankingRepository.getUserRankingList(
                        organizationId = null,
                        isWeekly = true
                    ).cachedIn(viewModelScope)
                )
            }
        }
    }
}
