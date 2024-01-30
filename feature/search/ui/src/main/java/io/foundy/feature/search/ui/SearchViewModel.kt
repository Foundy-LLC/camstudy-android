package io.foundy.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.feature.room_list.data.repository.RoomListRepository
import io.foundy.feature.search.data.repository.SearchRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(OrbitExperimental::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val roomListRepository: RoomListRepository,
    private val searchRepository: SearchRepository
) : ViewModel(), ContainerHost<SearchUiState, SearchSideEffect> {

    override val container: Container<SearchUiState, SearchSideEffect> = container(
        SearchUiState(
            searchedRoomFlow = roomListRepository.getRooms("").cachedIn(viewModelScope),
            onQueryChanged = ::updateQueryInput,
            onSearchClick = ::search,
            onSelectChip = ::updateSelectedChip
        )
    )

    private var searchUserJob: Job? = null
    private var searchRoomJob: Job? = null

    private fun updateQueryInput(query: String) = blockingIntent {
        reduce { state.copy(query = query) }
        search(query = query, debounceMillis = 300)
    }

    private fun updateSelectedChip(chip: SearchChip) = intent {
        reduce { state.copy(selectedChip = chip) }
        search(query = state.query)
    }

    private fun search(query: String, debounceMillis: Long = 0) = intent {
        when (state.selectedChip) {
            SearchChip.User -> searchUsers(query, debounceMillis = debounceMillis)
            SearchChip.StudyRoom -> searchRooms(query, debounceMillis = debounceMillis)
        }
    }

    private fun searchUsers(query: String, debounceMillis: Long = 0) = intent {
        if (query.isEmpty()) {
            return@intent
        }
        searchUserJob?.cancel()
        searchUserJob = viewModelScope.launch {
            delay(debounceMillis)
            reduce { state.copy(isUserRefreshing = true) }
            searchRepository.searchUsers(userName = query).onSuccess {
                reduce { state.copy(searchedUsers = it) }
            }.onFailure {
                if (it is CancellationException) {
                    return@onFailure
                }
                postSideEffect(
                    SearchSideEffect.Message(
                        content = it.message,
                        defaultStringRes = R.string.failed_to_search_user
                    )
                )
            }
            reduce { state.copy(isUserRefreshing = false) }
        }
    }

    private fun searchRooms(query: String, debounceMillis: Long = 0) = intent {
        searchRoomJob?.cancel()
        searchRoomJob = viewModelScope.launch {
            delay(debounceMillis)
            reduce {
                state.copy(
                    searchedRoomFlow = roomListRepository.getRooms(query).cachedIn(viewModelScope)
                )
            }
        }
    }
}
