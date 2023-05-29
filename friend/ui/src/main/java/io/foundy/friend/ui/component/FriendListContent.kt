package io.foundy.friend.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.UserOverview
import io.foundy.core.ui.pullrefresh.RefreshableContent
import io.foundy.friend.ui.FriendRecommendTabUiState
import io.foundy.friend.ui.R

@Composable
fun FriendListContent(
    users: LazyPagingItems<UserOverview>,
    recommendTabUiState: FriendRecommendTabUiState,
    onUserClick: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val isFriendListLoading = users.loadState.refresh is LoadState.Loading
    val hasRecommendedUser = !recommendTabUiState.isLoading &&
        recommendTabUiState.recommendedUsers.isNotEmpty()

    RefreshableContent(
        modifier = Modifier.fillMaxSize(),
        refreshing = isFriendListLoading,
        onRefresh = onRefresh
    ) {
        if (users.itemCount == 0 && !isFriendListLoading) {
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
                        onClick = { onUserClick(it.id) },
                        showDivider = users.itemCount - 1 != index
                    )
                }
                if (hasRecommendedUser && !isFriendListLoading) {
                    item {
                        Spacer(
                            modifier = Modifier
                                .height(8.dp)
                                .fillMaxWidth()
                                .background(color = CamstudyTheme.colorScheme.systemUi01)
                        )
                        CamstudyText(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            text = stringResource(R.string.recommendation_for_you),
                            style = CamstudyTheme.typography.titleMedium.copy(
                                color = CamstudyTheme.colorScheme.systemUi07,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        CamstudyDivider()
                    }
                    items(items = recommendTabUiState.recommendedUsers) { user ->
                        RecommendedUserTile(
                            user = user,
                            enabledActionButton = !recommendTabUiState.inPendingUserIds.contains(
                                element = user.id
                            ),
                            onUserClick = { onUserClick(user.id) },
                            onRemoveFriend = recommendTabUiState.onRemoveFriend,
                            onCancelRequest = recommendTabUiState.onCancelRequest,
                            onAcceptFriend = recommendTabUiState.onAcceptFriend,
                            onRequestFriend = recommendTabUiState.onRequestFriend
                        )
                    }
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
