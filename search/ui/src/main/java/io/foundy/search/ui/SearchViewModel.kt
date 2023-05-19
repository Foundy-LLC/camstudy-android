package io.foundy.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.search.data.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel(), ContainerHost<SearchUiState, SearchSideEffect> {

    override val container: Container<SearchUiState, SearchSideEffect> = container(
        SearchUiState(
            onQueryChanged = ::updateQueryInput,
            onSearchClick = ::search,
            onSelectChip = ::updateSelectedChip
        )
    )

    private var searchJob: Job? = null

    private fun updateQueryInput(query: String) = intent {
        reduce { state.copy(query = query) }
        search(query = query, debounceMillis = 300)
    }

    private fun updateSelectedChip(chip: SearchChip) = intent {
        reduce { state.copy(selectedChip = chip) }
        search(query = state.query)
    }

    private fun search(query: String, debounceMillis: Long = 0) = intent {
        if (query.isNotEmpty()) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(debounceMillis)
                when (state.selectedChip) {
                    SearchChip.User -> searchUsers(query)
                    SearchChip.StudyRoom -> TODO()
                }
            }
        }
    }

    private fun searchUsers(query: String) = intent {
        searchRepository.searchUsers(userName = query).onSuccess {
            reduce { state.copy(searchedUsers = it) }
        }.onFailure {
            postSideEffect(
                SearchSideEffect.Message(
                    content = it.message,
                    defaultStringRes = R.string.failed_to_search_user
                )
            )
        }
    }
}
