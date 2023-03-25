package io.foundy.search.ui

import io.foundy.core.model.SearchedUser

data class SearchUiState(
    val searchedUsers: List<SearchedUser> = emptyList(),
    val actionPendingUserIds: List<String> = emptyList(),
    val query: String = "",
    val onQueryChanged: (String) -> Unit,
    val onSearchClick: (String) -> Unit,
    val onFriendRequestClick: (id: String) -> Unit,
    val onCancelFriendRequestClick: (id: String) -> Unit,
    val onRemoveFriendClick: (id: String) -> Unit
)
