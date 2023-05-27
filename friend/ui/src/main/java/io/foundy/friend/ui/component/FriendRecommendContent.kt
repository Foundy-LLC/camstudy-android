package io.foundy.friend.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.ui.pullrefresh.RefreshableContent
import io.foundy.friend.ui.FriendRecommendTabUiState
import io.foundy.friend.ui.R

@Composable
fun FriendRecommendContent(
    uiState: FriendRecommendTabUiState,
    onUserClick: (String) -> Unit
) {
    RefreshableContent(
        modifier = Modifier.fillMaxSize(),
        refreshing = uiState.isLoading,
        onRefresh = uiState.fetchRecommendedUsers
    ) {
        if (uiState.recommendedUsers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                CamstudyText(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    text = stringResource(R.string.empty_recommended_friends),
                    style = CamstudyTheme.typography.headlineSmall.copy(
                        color = CamstudyTheme.colorScheme.systemUi04,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.recommendedUsers) { user ->
                    UserTile(
                        user = user,
                        onClick = { onUserClick(user.id) },
                        leading = {
                            IconButton(
                                modifier = Modifier.padding(end = 4.dp),
                                onClick = { uiState.onRequestFriend(user.id) },
                                enabled = !uiState.inPendingUserIds.contains(user.id)
                            ) {
                                CamstudyIcon(
                                    icon = CamstudyIcons.PersonAdd,
                                    contentDescription = null,
                                    tint = CamstudyTheme.colorScheme.systemUi08
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
