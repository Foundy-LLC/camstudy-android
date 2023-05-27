package io.foundy.friend.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.UserOverview
import io.foundy.core.ui.pullrefresh.RefreshableContent
import io.foundy.friend.ui.R

@Composable
fun FriendListContent(
    users: LazyPagingItems<UserOverview>,
    onUserClick: (String) -> Unit,
    onRefresh: () -> Unit
) {
    RefreshableContent(
        modifier = Modifier.fillMaxSize(),
        refreshing = users.loadState.refresh is LoadState.Loading,
        onRefresh = onRefresh
    ) {
        if (users.itemCount == 0) {
            EmptyFriends()
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(
                    count = users.itemCount,
                    key = users.itemKey { it.id }
                ) { index ->
                    val user = users[index] ?: return@items

                    UserTile(
                        user = user,
                        onClick = { onUserClick(it.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFriends() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CamstudyText(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            text = stringResource(R.string.there_is_no_friend),
            style = CamstudyTheme.typography.headlineSmall.copy(
                color = CamstudyTheme.colorScheme.systemUi04,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
