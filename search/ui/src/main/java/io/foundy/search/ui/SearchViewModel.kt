package io.foundy.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.model.FriendStatus
import io.foundy.friend.data.repository.FriendRepository
import io.foundy.search.data.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val friendRepository: FriendRepository
) : ViewModel(), ContainerHost<SearchUiState, SearchSideEffect> {

    override val container: Container<SearchUiState, SearchSideEffect> = container(
        SearchUiState(
            onQueryChanged = ::updateQueryInput,
            onSearchClick = ::forceSearchUsers,
            onFriendRequestClick = ::requestFriend,
            onRemoveFriendClick = ::removeFriend
        )
    )

    private var searchJob: Job? = null

    private fun forceSearchUsers(query: String) = intent {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchUsers(query)
        }
    }

    private fun searchUsersWithDebounce(query: String) = intent {
        // TODO: 디바운스 동작이 원할하지 않음. 마지막 요청이 누락되기 때문에 수정 필요함.
        if (searchJob?.isCompleted != false) {
            searchJob = viewModelScope.launch {
                searchUsers(query)
                delay(300)
            }
        }
    }

    private fun searchUsers(query: String) = intent {
        searchRepository.searchUsers(userName = query).onSuccess {
            reduce { state.copy(searchedUsers = it) }
        }.onFailure {
            postSideEffect(
                SearchSideEffect.Message(
                    content = it.message,
                    defaultStringRes = R.string.failed_to_search_user
                )
            )
        }
    }

    private fun updateQueryInput(query: String) = intent {
        reduce { state.copy(query = query) }
        if (query.isNotEmpty()) {
            searchUsersWithDebounce(query = query)
        }
    }

    private fun requestFriend(userId: String) = intent {
        reduce { state.copy(actionPendingUserIds = state.actionPendingUserIds + userId) }
        friendRepository.requestFriend(targetUserId = userId).onSuccess {
            val newSearchedUser = state.searchedUsers.map { user ->
                if (user.id == userId) {
                    return@map user.copy(friendStatus = FriendStatus.REQUESTED)
                }
                return@map user
            }
            reduce { state.copy(searchedUsers = newSearchedUser) }
        }.onFailure {
            postSideEffect(
                SearchSideEffect.Message(
                    content = it.message,
                    defaultStringRes = R.string.failed_to_search_user
                )
            )
        }
        reduce {
            state.copy(
                actionPendingUserIds = state.actionPendingUserIds.filterNot { it == userId }
            )
        }
    }

    private fun removeFriend(userId: String) = intent {
        // TODO
    }
}
