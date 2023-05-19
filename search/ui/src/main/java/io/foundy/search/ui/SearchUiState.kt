package io.foundy.search.ui

import io.foundy.core.model.SearchedUser

data class SearchUiState(
    val query: String = "",
    val onQueryChanged: (String) -> Unit,
    val onSearchClick: (String) -> Unit,
    val searchedUsers: List<SearchedUser> = emptyList(),
    val selectedChip: SearchChip = SearchChip.User,
    val onSelectChip: (SearchChip) -> Unit
)

enum class SearchChip {
    User,
    StudyRoom
}
