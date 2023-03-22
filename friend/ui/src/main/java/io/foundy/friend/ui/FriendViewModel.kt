package io.foundy.friend.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.friend.data.repository.FriendRepository
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel(), ContainerHost<FriendUiState, FriendSideEffect> {

    override val container: Container<FriendUiState, FriendSideEffect> = container(FriendUiState())

    fun bind(userId: String) = intent {
        val flow = friendRepository.getFriends(userId = userId)
        reduce { state.copy(pagingDataFlow = flow) }
    }
}
