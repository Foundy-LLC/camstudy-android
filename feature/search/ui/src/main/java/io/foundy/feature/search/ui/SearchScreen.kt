package io.foundy.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.found.feature.user.ui.UserProfileDialog
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyFilterChip
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.FriendStatus
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.SearchedUser
import io.foundy.core.ui.RoomTileWithJoinButton
import io.foundy.core.ui.UserProfileImage
import io.foundy.core.ui.collectAsLazyPagingItems
import io.foundy.core.ui.pullrefresh.RefreshableContent
import io.foundy.feature.room.ui.RoomActivity
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun SearchRoute(
    navigator: DestinationsNavigator,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var userIdForShowDialog by remember { mutableStateOf<String?>(null) }
    val (rooms, refreshRooms) = uiState.searchedRoomFlow.collectAsLazyPagingItems()

    viewModel.collectSideEffect {
        when (it) {
            is SearchSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it.content ?: context.getString(it.defaultStringRes)
                )
            }
        }
    }

    userIdForShowDialog?.let { id ->
        UserProfileDialog(userId = id, onCancel = { userIdForShowDialog = null })
    }

    SearchScreen(
        uiState = uiState,
        rooms = rooms,
        snackbarHostState = snackbarHostState,
        popBackStack = { navigator.popBackStack() },
        onUserClick = { userIdForShowDialog = it },
        onRoomJoinClick = { room ->
            val intent = RoomActivity.getIntent(context, room)
            context.startActivity(intent)
        },
        onRoomRefresh = { refreshRooms() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    rooms: LazyPagingItems<RoomOverview>,
    onRoomRefresh: () -> Unit,
    onUserClick: (String) -> Unit,
    onRoomJoinClick: (RoomOverview) -> Unit,
    popBackStack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            CamstudyTopAppBar(
                onBackClick = popBackStack,
                height = 56.dp,
                title = {
                    CamstudyTextField(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(end = 14.dp)
                            .focusRequester(focusRequester),
                        value = uiState.query,
                        onValueChange = uiState.onQueryChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                uiState.onSearchClick(uiState.query)
                                keyboardController?.hide()
                            }
                        ),
                        placeholder = stringResource(R.string.search_text_field_placeholder)
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Chips(
                    selectedChip = uiState.selectedChip,
                    onSelectChip = uiState.onSelectChip
                )

                when (uiState.selectedChip) {
                    SearchChip.User -> UserList(
                        users = uiState.searchedUsers,
                        isUserRefreshing = uiState.isUserRefreshing,
                        onUserClick = onUserClick,
                        onRefresh = { uiState.onSearchClick(uiState.query) }
                    )
                    SearchChip.StudyRoom -> StudyRoomList(
                        rooms = rooms,
                        onJoinClick = onRoomJoinClick,
                        onRefresh = onRoomRefresh
                    )
                }
            }
        }
    }
}

@Composable
private fun UserList(
    users: List<SearchedUser>,
    isUserRefreshing: Boolean,
    onRefresh: () -> Unit,
    onUserClick: (String) -> Unit
) {
    RefreshableContent(
        modifier = Modifier
            .fillMaxSize()
            .background(color = CamstudyTheme.colorScheme.systemBackground),
        onRefresh = onRefresh,
        refreshing = isUserRefreshing
    ) {
        if (users.isEmpty() && !isUserRefreshing) {
            EmptyText()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(users, key = { it.id }) { user ->
                    UserTile(searchedUser = user, onClick = { onUserClick(user.id) })
                }
            }
        }
    }
}

@Composable
fun StudyRoomList(
    rooms: LazyPagingItems<RoomOverview>,
    onRefresh: () -> Unit,
    onJoinClick: (RoomOverview) -> Unit
) {
    val isRoomRefreshing = rooms.loadState.refresh is LoadState.Loading

    RefreshableContent(
        refreshing = isRoomRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier
            .fillMaxSize()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
    ) {
        if (rooms.itemCount == 0 && !isRoomRefreshing) {
            EmptyText()
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(count = rooms.itemCount, key = rooms.itemKey { it.id }) { index ->
                    val room = rooms[index] ?: return@items

                    Box {
                        RoomTileWithJoinButton(
                            room = room,
                            onJoinClick = onJoinClick
                        )
                        CamstudyDivider(Modifier.align(Alignment.BottomCenter))
                    }
                }
            }
        }
    }
}

@Composable
private fun UserTile(
    searchedUser: SearchedUser,
    onClick: () -> Unit
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = CamstudyTheme.colorScheme.systemBackground)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserProfileImage(model = searchedUser.profileImage)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = searchedUser.name,
                    style = CamstudyTheme.typography.titleSmall.copy(
                        color = CamstudyTheme.colorScheme.systemUi08,
                        fontWeight = FontWeight.Medium
                    )
                )
                searchedUser.introduce?.let {
                    Text(
                        text = it,
                        style = CamstudyTheme.typography.labelMedium.copy(
                            color = CamstudyTheme.colorScheme.systemUi05,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
        CamstudyDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun Chips(selectedChip: SearchChip, onSelectChip: (SearchChip) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = CamstudyTheme.colorScheme.systemBackground)
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(items = SearchChip.values()) { index, chip ->
                CamstudyFilterChip(
                    selected = chip == selectedChip,
                    onClick = { onSelectChip(chip) },
                    label = { CamstudyText(text = stringResource(id = chip.labelRes)) }
                )
                if (index != SearchChip.values().size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
        CamstudyDivider(Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun EmptyText() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
    ) {
        CamstudyText(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(R.string.empty),
            style = CamstudyTheme.typography.displayMedium.copy(
                color = CamstudyTheme.colorScheme.systemUi03,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    val (rooms) = emptyFlow<PagingData<RoomOverview>>().collectAsLazyPagingItems()

    CamstudyTheme {
        SearchScreen(
            uiState = SearchUiState(
                query = "김민성",
                searchedRoomFlow = emptyFlow(),
                onSearchClick = {},
                onQueryChanged = {},
                onSelectChip = {}
            ),
            rooms = rooms,
            onUserClick = {},
            popBackStack = {},
            snackbarHostState = SnackbarHostState(),
            onRoomJoinClick = {},
            onRoomRefresh = {}
        )
    }
}

@Preview
@Composable
private fun UserTilePreview() {
    CamstudyTheme {
        UserTile(
            searchedUser = SearchedUser(
                id = "id",
                name = "김민성",
                introduce = "안녕하세요",
                profileImage = null,
                friendStatus = FriendStatus.NONE
            ),
            onClick = {}
        )
    }
}
