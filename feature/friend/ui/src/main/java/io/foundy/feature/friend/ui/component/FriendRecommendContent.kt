package io.foundy.feature.friend.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextButton
import io.foundy.core.designsystem.component.camstudyTextButtonColors
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.FriendStatus
import io.foundy.core.model.RecommendedUser
import io.foundy.core.model.UserOverview
import io.foundy.core.ui.pullrefresh.RefreshableContent
import io.foundy.feature.friend.ui.FriendRecommendTabUiState
import io.foundy.feature.friend.ui.R

@Composable
fun FriendRecommendContent(
    uiState: FriendRecommendTabUiState,
    onUserClick: (String) -> Unit
) {
    var userIdToRemoveFriend by remember { mutableStateOf<String?>(null) }

    userIdToRemoveFriend?.let { userId ->
        CamstudyDialog(
            content = stringResource(id = R.string.are_you_sure_dismiss),
            onDismissRequest = { userIdToRemoveFriend = null },
            onCancel = { userIdToRemoveFriend = null },
            onConfirm = {
                userIdToRemoveFriend = null
                uiState.onRemoveFriend(userId)
            }
        )
    }

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
                    RecommendedUserTile(
                        user = user,
                        enabledActionButton = !uiState.inPendingUserIds.contains(user.id),
                        onUserClick = { onUserClick(user.id) },
                        onRemoveFriend = uiState.onRemoveFriend,
                        onCancelRequest = uiState.onCancelRequest,
                        onAcceptFriend = uiState.onAcceptFriend,
                        onRequestFriend = uiState.onRequestFriend
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendedUserTile(
    user: RecommendedUser,
    enabledActionButton: Boolean,
    onUserClick: (String) -> Unit,
    onRequestFriend: (String) -> Unit,
    onAcceptFriend: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onRemoveFriend: (String) -> Unit
) {
    UserTile(
        user = UserOverview(
            id = user.id,
            name = user.name,
            introduce = user.introduce,
            profileImage = user.profileImage
        ),
        onClick = { onUserClick(user.id) },
        leading = {
            val (
                label: String,
                labelColor: Color,
                onClick: (String) -> Unit
            ) = when (user.friendStatus) {
                FriendStatus.NONE -> Triple(
                    stringResource(R.string.action_button_request_friend),
                    CamstudyTheme.colorScheme.primary,
                    onRequestFriend
                )
                FriendStatus.REQUESTED -> Triple(
                    stringResource(R.string.action_button_cancel_request),
                    CamstudyTheme.colorScheme.systemUi05,
                    onCancelRequest
                )
                FriendStatus.REQUEST_RECEIVED -> Triple(
                    stringResource(R.string.action_button_accept_request),
                    CamstudyTheme.colorScheme.primary,
                    onAcceptFriend
                )
                FriendStatus.ACCEPTED -> Triple(
                    stringResource(R.string.action_button_remove_friend),
                    CamstudyTheme.colorScheme.systemUi05,
                    onRemoveFriend
                )
            }
            CamstudyTextButton(
                modifier = Modifier.padding(end = 4.dp),
                label = label,
                enabled = enabledActionButton,
                colors = ButtonDefaults.camstudyTextButtonColors(
                    contentColor = labelColor
                ),
                onClick = { onClick(user.id) }
            )
        }
    )
}
