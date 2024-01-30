package io.foundy.feature.room_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.feature.room_list.data.repository.RoomListRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(OrbitExperimental::class)
@HiltViewModel
class RoomListViewModel @Inject constructor(
    private val roomListRepository: RoomListRepository
) : ViewModel(), ContainerHost<RoomListUiState, RoomListSideEffect> {

    override val container: Container<RoomListUiState, RoomListSideEffect> = container(
        RoomListUiState(
            roomPagingDataStream = roomListRepository.getRooms("").cachedIn(viewModelScope),
            onSearchQueryChange = ::updateSearchQuery,
            onRefresh = ::refresh
        )
    )

    private var roomSearchJob: Job? = null

    private fun refresh() = intent {
        updateSearchQuery(state.searchQuery, delayMillis = 0)
    }

    private fun updateSearchQuery(query: String, delayMillis: Long = 300) = blockingIntent {
        reduce { state.copy(searchQuery = query) }
        searchRooms(query = query, delayMillis = delayMillis)
    }

    private fun searchRooms(query: String, delayMillis: Long = 300) = intent {
        roomSearchJob?.cancel()
        roomSearchJob = viewModelScope.launch {
            delay(delayMillis)
            val roomsStream = roomListRepository.getRooms(query)
            reduce { state.copy(roomPagingDataStream = roomsStream) }
        }
    }
}
