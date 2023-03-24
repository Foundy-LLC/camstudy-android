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
    private val repository: SearchRepository
) : ViewModel(), ContainerHost<SearchUiState, SearchSideEffect> {

    override val container: Container<SearchUiState, SearchSideEffect> = container(
        SearchUiState(
            onQueryChanged = ::updateQueryInput,
            onSearchClick = ::forceSearchUsers
        )
    )

    private var searchJob: Job? = null

    private fun forceSearchUsers(query: String) = intent {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchUsers(query)
        }
    }

    private fun searchUsersWithDebounce(query: String) = intent {
        if (searchJob?.isCompleted != false) {
            searchJob = viewModelScope.launch {
                searchUsers(query)
                delay(300)
            }
        }
    }

    private fun searchUsers(query: String) = intent {
        repository.searchUsers(userName = query).onSuccess {
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

    private fun updateQueryInput(query: String) = intent {
        reduce { state.copy(query = query) }
        if (query.isNotEmpty()) {
            searchUsersWithDebounce(query = query)
        }
    }
}
