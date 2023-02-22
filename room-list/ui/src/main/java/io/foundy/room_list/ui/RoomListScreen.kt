package io.foundy.room_list.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.core.model.RoomOverview
import io.foundy.room.ui.destinations.RoomRouteDestination
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun RoomListRoute(
    parentNavigator: DestinationsNavigator,
    viewModel: RoomListViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value

    RoomListScreen(
        uiState = uiState,
        onRoomClick = { id -> parentNavigator.navigate(RoomRouteDestination(id = id)) }
    )
}

@Composable
fun RoomListScreen(
    uiState: RoomListUiState,
    onRoomClick: (id: String) -> Unit
) {
    val rooms = uiState.roomPagingDataStream.collectAsLazyPagingItems()

    LazyColumn {
        item {
            Text("방목록")
        }
        items(
            items = rooms,
            key = { it.id }
        ) { roomOverview ->
            if (roomOverview == null) {
                Text("End")
                return@items
            }
            RoomItem(room = roomOverview, onClick = { onRoomClick(roomOverview.id) })
        }
        when (rooms.loadState.refresh) { // FIRST LOAD
            is LoadState.Error -> errorItem(rooms.loadState)
            is LoadState.Loading -> loadingItem()
            else -> {}
        }
        when (rooms.loadState.append) { // Pagination
            is LoadState.Error -> errorItem(rooms.loadState)
            is LoadState.Loading -> loadingItem()
            else -> {}
        }
    }
}

@Composable
private fun RoomItem(
    room: RoomOverview,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Text(room.title)
    }
}

private fun LazyListScope.errorItem(loadState: CombinedLoadStates) {
    val error = loadState.refresh as LoadState.Error
    item {
        val message = error.error.message ?: stringResource(R.string.unknown_error_caused)
        Text(text = message)
    }
}

private fun LazyListScope.loadingItem() {
    item {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Pagination Loading")
            CircularProgressIndicator()
        }
    }
}
