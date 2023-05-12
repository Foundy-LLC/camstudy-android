package io.foundy.room_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.room_list.data.repository.RoomListRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class RoomListViewModel @Inject constructor(
    private val roomListRepository: RoomListRepository
) : ViewModel(), ContainerHost<RoomListUiState, RoomListSideEffect> {

    override val container: Container<RoomListUiState, RoomListSideEffect> = container(
        RoomListUiState(
            roomPagingDataStream = roomListRepository.getRooms("").cachedIn(viewModelScope),
            onSearchQueryChange = ::updateSearchQuery,
        )
    )

    private var roomSearchJob: Job? = null

    private fun updateSearchQuery(query: String) = intent {
        reduce { state.copy(searchQuery = query) }
        searchRooms(query = query)
    }

    private fun searchRooms(query: String) = intent {
        roomSearchJob?.cancel()
        roomSearchJob = viewModelScope.launch {
            delay(300)
            val roomsStream = roomListRepository.getRooms(query)
            reduce { state.copy(roomPagingDataStream = roomsStream) }
        }
    }
}
