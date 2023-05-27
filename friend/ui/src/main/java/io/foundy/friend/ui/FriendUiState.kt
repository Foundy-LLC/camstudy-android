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
    val friendPagingData: Flow<PagingData<UserOverview>> = emptyFlow()
)

data class FriendRecommendTabUiState(
    val isLoading: Boolean = true,
    val recommendedUsers: List<UserOverview> = emptyList(),
    val inPendingUserIds: List<String> = emptyList(),
    val fetchRecommendedUsers: () -> Unit,
    val onRequestFriend: (String) -> Unit,
)

data class RequestedFriendTabUiState(
    val requesterPagingData: Flow<PagingData<UserOverview>> = emptyFlow(),
    val inPendingUserIds: List<String> = emptyList(),
    val onAcceptClick: (String) -> Unit,
    val onRejectClick: (String) -> Unit,
)
