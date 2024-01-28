package io.foundy.search.ui

import androidx.annotation.StringRes
import androidx.paging.PagingData
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.SearchedUser
import kotlinx.coroutines.flow.Flow

data class SearchUiState(
    val query: String = "",
    val onQueryChanged: (String) -> Unit,
    val onSearchClick: (String) -> Unit,
    val searchedUsers: List<SearchedUser> = emptyList(),
    val isUserRefreshing: Boolean = false,
    val searchedRoomFlow: Flow<PagingData<RoomOverview>>,
    val selectedChip: SearchChip = SearchChip.User,
    val onSelectChip: (SearchChip) -> Unit
)

enum class SearchChip(@StringRes val labelRes: Int) {
    User(R.string.chip_user),
    StudyRoom(R.string.chip_study_room)
}
