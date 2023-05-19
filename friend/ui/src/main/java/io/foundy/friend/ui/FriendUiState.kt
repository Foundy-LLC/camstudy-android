package io.foundy.friend.ui

import androidx.paging.PagingData
import io.foundy.core.model.UserOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class FriendUiState(
    val friendListTabUiState: FriendListTabUiState,
    val friendRecommendTabUiState: FriendRecommendTabUiState,
    val requestedFriendTabUiState: RequestedFriendTabUiState
)

data class FriendListTabUiState(
    val friendPagingData: Flow<PagingData<UserOverview>> = emptyFlow(),
    val inRemovingUserIds: List<String> = emptyList(),
    val onRemoveFriendClick: (String) -> Unit,
)

data class FriendRecommendTabUiState(
    val userPagingData: Flow<PagingData<UserOverview>> = emptyFlow(),
    val inPendingUserIds: List<String> = emptyList(),
    val friendRequestedUserIds: List<String> = emptyList(),
    val onRequestFriend: (String) -> Unit,
)

data class RequestedFriendTabUiState(
    val requesterPagingData: Flow<PagingData<UserOverview>> = emptyFlow(),
    val inPendingUserIds: List<String> = emptyList(),
    val onAcceptClick: (String) -> Unit,
    val onRejectClick: (String) -> Unit,
)
