package io.foundy.room_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.room_list.data.repository.RoomListRepository
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

    override val container: Container<RoomListUiState, RoomListSideEffect> =
        container(RoomListUiState())

    init {
        intent {
            val roomsStream = roomListRepository.getRooms().cachedIn(viewModelScope)
            reduce { state.copy(roomPagingDataStream = roomsStream) }
        }
    }
}
