package io.foundy.search.ui

import io.foundy.core.model.SearchedUser

data class SearchUiState(
    val searchedUsers: List<SearchedUser> = emptyList(),
    val query: String = "",
    val onQueryChanged: (String) -> Unit,
    val onSearchClick: (String) -> Unit
)
