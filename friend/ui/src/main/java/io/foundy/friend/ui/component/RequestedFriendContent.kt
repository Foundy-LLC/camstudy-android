package io.foundy.friend.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextButton
import io.foundy.core.designsystem.component.camstudyTextButtonColors
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.UserOverview
import io.foundy.friend.ui.R
import io.foundy.friend.ui.RequestedFriendTabUiState

@Composable
fun RequestedFriendContent(
    uiState: RequestedFriendTabUiState,
    users: LazyPagingItems<UserOverview>,
    onUserClick: (String) -> Unit
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
                val isInPending = uiState.inPendingUserIds.contains(user.id)

                UserTile(
                    user = user,
                    onClick = { onUserClick(it.id) },
                    leading = {
                        Row {
                            CamstudyTextButton(
                                modifier = Modifier.padding(end = 4.dp),
                                onClick = { uiState.onRejectClick(user.id) },
                                enabled = !isInPending,
                                colors = ButtonDefaults.camstudyTextButtonColors(
                                    contentColor = CamstudyTheme.colorScheme.systemUi03
                                ),
                                label = stringResource(R.string.reject)
                            )
                            CamstudyTextButton(
                                modifier = Modifier.padding(end = 4.dp),
                                onClick = { uiState.onAcceptClick(user.id) },
                                enabled = !isInPending,
                                label = stringResource(R.string.accept)
                            )
                        }
                    }
                )
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
            text = stringResource(R.string.there_is_no_friend_request),
            style = CamstudyTheme.typography.headlineSmall.copy(
                color = CamstudyTheme.colorScheme.systemUi04,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
