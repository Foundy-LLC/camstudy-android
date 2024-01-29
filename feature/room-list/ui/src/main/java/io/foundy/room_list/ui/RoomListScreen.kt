package io.foundy.room_list.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyExtendedFloatingActionButton
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.component.FloatingActionButtonBottomPadding
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.RoomOverview
import io.foundy.core.ui.RoomTileWithJoinButton
import io.foundy.core.ui.isScrollingUp
import io.foundy.core.ui.pullrefresh.RefreshableContent
import io.foundy.room.ui.RoomActivity
import io.foundy.room_list.ui.create.destinations.RoomCreateScreenDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun RoomListRoute(
    navigator: DestinationsNavigator,
    viewModel: RoomListViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val rooms = uiState.roomPagingDataStream.collectAsLazyPagingItems()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectSideEffect {
        when (it) {
            is RoomListSideEffect.Message -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(it.content ?: context.getString(it.defaultRes))
            }
            is RoomListSideEffect.SuccessToCreateRoom -> {
                val intent = RoomActivity.getIntent(context, roomOverview = it.createdRoom)
                context.startActivity(intent)
            }
        }
    }

    RoomListScreen(
        uiState = uiState,
        rooms = rooms,
        snackbarHostState = snackbarHostState,
        onRoomJoinClick = { room ->
            val intent = RoomActivity.getIntent(context, roomOverview = room)
            context.startActivity(intent)
        },
        onRoomCreateClick = {
            navigator.navigate(RoomCreateScreenDestination)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    uiState: RoomListUiState,
    rooms: LazyPagingItems<RoomOverview>,
    snackbarHostState: SnackbarHostState,
    onRoomJoinClick: (room: RoomOverview) -> Unit,
    onRoomCreateClick: () -> Unit
) {
    val listState = rememberLazyListState()

    RefreshableContent(
        refreshing = rooms.loadState.refresh is LoadState.Loading,
        onRefresh = uiState.onRefresh
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = CamstudyTheme.colorScheme.systemBackground,
            floatingActionButton = {
                CamstudyExtendedFloatingActionButton(
                    onClick = onRoomCreateClick,
                    expanded = listState.isScrollingUp(),
                    text = { Text(text = stringResource(R.string.room_create_app_bar_title)) },
                    icon = {
                        CamstudyIcon(icon = CamstudyIcons.Add, contentDescription = null)
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                state = listState
            ) {
                headerItem(query = uiState.searchQuery, onQueryChange = uiState.onSearchQueryChange)
                items(
                    count = rooms.itemCount,
                    key = rooms.itemKey { it.id },
                ) { index ->
                    val roomOverview = rooms[index] ?: return@items
                    Box {
                        RoomTileWithJoinButton(
                            modifier = Modifier.fillMaxWidth(),
                            room = roomOverview,
                            onJoinClick = onRoomJoinClick
                        )
                        CamstudyDivider()
                    }
                }
                item { CamstudyDivider() }
                when (rooms.loadState.refresh) { // FIRST LOAD
                    is LoadState.Error -> errorItem(rooms.loadState)
                    else -> {}
                }
                when (rooms.loadState.append) { // Pagination
                    is LoadState.Error -> errorItem(rooms.loadState)
                    else -> {}
                }
                item { Spacer(modifier = Modifier.height(FloatingActionButtonBottomPadding)) }
            }
        }
    }
}

private fun LazyListScope.headerItem(
    query: String,
    onQueryChange: (String) -> Unit
) {
    item {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 12.dp
            )
        ) {
            CamstudyText(
                text = stringResource(R.string.study_room_list_title),
                style = CamstudyTheme.typography.titleMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi07
                )
            )
            Box(Modifier.height(12.dp))
            CamstudyTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = stringResource(R.string.room_search_placeholder),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onQueryChange(query) })
            )
        }
    }
}

private fun LazyListScope.errorItem(loadState: CombinedLoadStates) {
    val error = loadState.refresh as? LoadState.Error ?: return
    item {
        val message = error.error.message ?: stringResource(R.string.unknown_error_caused)
        Text(text = message)
    }
}
