package io.foundy.friend.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import io.foundy.core.model.UserOverview
import org.orbitmvi.orbit.compose.collectAsState

// TODO: 친구 요청 목록 페이지

@Destination
@Composable
fun FriendRoute(
    userId: String,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value

    LaunchedEffect(userId) {
        viewModel.bind(userId = userId)
    }

    FriendScreen(
        uiState = uiState
    )
}

@Composable
fun FriendScreen(
    uiState: FriendUiState
) {
    val items = uiState.pagingDataFlow.collectAsLazyPagingItems()

    LazyColumn {
        items(items = items, key = { it.id }) { user ->
            if (user == null) {
                Text("end")
                return@items
            }
            User(user = user)
        }
        when (items.loadState.refresh) { // FIRST LOAD
            is LoadState.Error -> errorItem(items.loadState)
            is LoadState.Loading -> loadingItem()
            else -> {}
        }
        when (items.loadState.append) { // Pagination
            is LoadState.Error -> errorItem(items.loadState)
            is LoadState.Loading -> loadingItem()
            else -> {}
        }
    }
}

@Composable
private fun User(user: UserOverview) {
    Text(modifier = Modifier.padding(12.dp), text = user.name)
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
