package io.foundy.friend.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.UserOverview
import io.foundy.friend.ui.FriendListTabUiState
import io.foundy.friend.ui.R

@Composable
fun FriendListContent(
    uiState: FriendListTabUiState,
    users: LazyPagingItems<UserOverview>,
    onUserClick: (String) -> Unit
) {
    var userIdForRemoveFriend by remember { mutableStateOf<String?>(null) }

    userIdForRemoveFriend?.let { id ->
        CamstudyDialog(
            content = stringResource(R.string.are_you_sure_dismiss),
            onConfirm = {
                uiState.onRemoveFriendClick(id)
                userIdForRemoveFriend = null
            },
            onDismissRequest = { userIdForRemoveFriend = null },
            onCancel = { userIdForRemoveFriend = null },
            confirmText = stringResource(R.string.dismiss)
        )
    }

    if (users.itemCount == 0) {
        EmptyFriends()
    } else {
        LazyColumn(Modifier.fillMaxSize()) {
            items(
                count = users.itemCount,
                key = users.itemKey { it.id }
            ) { index ->
                val user = users[index] ?: return@items
                val isInRemoving = uiState.inRemovingUserIds.contains(user.id)

                UserTile(
                    user = user,
                    onClick = { onUserClick(it.id) },
                    leading = {
                        IconButton(
                            modifier = Modifier.padding(end = 4.dp),
                            onClick = { userIdForRemoveFriend = user.id },
                            enabled = !isInRemoving
                        ) {
                            CamstudyIcon(
                                icon = CamstudyIcons.PersonRemove,
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
