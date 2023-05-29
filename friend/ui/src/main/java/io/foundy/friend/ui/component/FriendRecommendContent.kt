package io.foundy.friend.ui.component

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
import io.foundy.core.model.UserOverview
import io.foundy.core.ui.pullrefresh.RefreshableContent
import io.foundy.friend.ui.FriendRecommendTabUiState
import io.foundy.friend.ui.R

@Composable
fun FriendRecommendContent(
    uiState: FriendRecommendTabUiState,
    onUserClick: (String) -> Unit
) {
    var userIdToRemoveFriend by remember { mutableStateOf<String?>(null) }

    userIdToRemoveFriend?.let { userId ->
        CamstudyDialog(
            content = "정말로 친구를 해제할까요?",
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
                                    "친구 요청",
                                    CamstudyTheme.colorScheme.primary,
                                    uiState.onRequestFriend
                                )
                                FriendStatus.REQUESTED -> Triple(
                                    "요청 취소",
                                    CamstudyTheme.colorScheme.systemUi05,
                                    uiState.onCancelRequest
                                )
                                FriendStatus.REQUEST_RECEIVED -> Triple(
                                    "친구 수락",
                                    CamstudyTheme.colorScheme.primary,
                                    uiState.onAcceptFriend
                                )
                                FriendStatus.ACCEPTED -> Triple(
                                    "친구 해제",
                                    CamstudyTheme.colorScheme.systemUi05
                                ) { userId -> userIdToRemoveFriend = userId }
                            }
                            CamstudyTextButton(
                                modifier = Modifier.padding(end = 4.dp),
                                label = label,
                                enabled = !uiState.inPendingUserIds.contains(user.id),
                                colors = ButtonDefaults.camstudyTextButtonColors(
                                    contentColor = labelColor
                                ),
                                onClick = { onClick(user.id) }
                            )
                        }
                    )
                }
            }
        }
    }
}
