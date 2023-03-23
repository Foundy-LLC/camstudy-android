package io.foundy.friend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.model.UserOverview
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

// TODO: 친구 요청 목록 페이지

@Destination
@Composable
fun FriendRoute(
    userId: String,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val friendRequests = uiState.friendRequestPagingData.collectAsLazyPagingItems()
    val friends = uiState.friendPagingData.collectAsLazyPagingItems()

    LaunchedEffect(userId) {
        viewModel.bind(userId = userId)
    }

    viewModel.collectSideEffect {
        when (it) {
            FriendSideEffect.OnSuccessToAccept -> {
                // TODO: 새로고침 안되는 중... 고쳐야함
                friendRequests.refresh()
                friends.refresh()
            }
            is FriendSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.content ?: context.getString(it.defaultStringRes)
                )
            }
        }
    }

    FriendScreen(
        friendRequests = friendRequests,
        friends = friends,
        inAcceptingIds = uiState.acceptingIds,
        onAcceptClick = uiState.onAcceptClick,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    friendRequests: LazyPagingItems<UserOverview>,
    friends: LazyPagingItems<UserOverview>,
    inAcceptingIds: List<String>,
    onAcceptClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            if (friendRequests.itemCount > 0) {
                item {
                    Text(text = "친구 요청 목록")
                }
                items(items = friendRequests, key = { it.id }) { user ->
                    if (user == null) {
                        Text("end")
                        return@items
                    }
                    FriendRequest(
                        user = user,
                        enabledAcceptButton = inAcceptingIds.none { it == user.id },
                        onAcceptClick = {
                            onAcceptClick(user.id)
                        }
                    )
                }
            }
            item {
                Text(text = "친구 목록")
            }
            items(items = friends, key = { it.id }) { user ->
                if (user == null) {
                    Text("end")
                    return@items
                }
                User(user = user)
            }
            when (friends.loadState.refresh) { // FIRST LOAD
                is LoadState.Error -> errorItem(friends.loadState)
                is LoadState.Loading -> loadingItem()
                else -> {}
            }
            when (friends.loadState.append) { // Pagination
                is LoadState.Error -> errorItem(friends.loadState)
                is LoadState.Loading -> loadingItem()
                else -> {}
            }
        }
    }
}

@Composable
private fun User(user: UserOverview) {
    Text(modifier = Modifier.padding(12.dp), text = user.name)
}

@Composable
private fun FriendRequest(
    user: UserOverview,
    enabledAcceptButton: Boolean,
    onAcceptClick: () -> Unit
) {
    Row {
        Text(modifier = Modifier.padding(12.dp), text = user.name)
        // TODO: 거절 기능 구현
        Button(onClick = onAcceptClick, enabled = enabledAcceptButton) {
            Text(text = "수락")
        }
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
