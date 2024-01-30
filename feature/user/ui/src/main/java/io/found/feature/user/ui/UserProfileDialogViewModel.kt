package io.found.feature.user.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.model.FriendStatus
import io.foundy.core.ui.UserMessage
import io.foundy.feature.friend.data.repository.FriendRepository
import io.foundy.feature.user.domain.usecase.GetUserUseCase
import io.foundy.feature.user.ui.R
import kotlinx.coroutines.CancellationException
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
class UserProfileDialogViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val friendRepository: FriendRepository
) : ViewModel(), ContainerHost<UserProfileDialogUiState, UserProfileDialogSideEffect> {

    override val container: Container<UserProfileDialogUiState, UserProfileDialogSideEffect> =
        container(UserProfileDialogUiState.Loading)

    private var friendActionTextResFetchJob: Job? = null
    private var fetchUserJob: Job? = null

    fun fetchUser(id: String) = intent {
        reduce { UserProfileDialogUiState.Loading }
        friendActionTextResFetchJob?.cancel()
        fetchUserJob?.cancel()
        fetchUserJob = viewModelScope.launch {
            getUserUseCase(id).onSuccess { user ->
                reduce {
                    UserProfileDialogUiState.Success(
                        user = user,
                        onRequestFriend = ::requestFriend,
                        onAcceptFriend = ::acceptFriendRequest,
                        onCancelFriendRequest = ::cancelRequest,
                        onCancelFriend = ::cancelFriend
                    )
                }
            }.onFailure {
                if (it is CancellationException) {
                    return@onFailure
                }
                reduce {
                    UserProfileDialogUiState.Failure(
                        message = UserMessage(
                            content = it.message,
                            defaultRes = R.string.user_dialog_failed_to_load_user
                        )
                    )
                }
            }
        }
    }

    private fun setFriendActionResultTextRes(@StringRes textRes: Int) = intent {
        friendActionTextResFetchJob?.cancel()
        friendActionTextResFetchJob = viewModelScope.launch {
            val uiState = state
            if (uiState !is UserProfileDialogUiState.Success) {
                return@launch
            }
            reduce { uiState.copy(friendActionSuccessMessageRes = textRes) }
            delay(FriendActionMessageDurationMilli)
            reduce { uiState.copy(friendActionSuccessMessageRes = null) }
        }
    }

    private fun setFriendActionErrorTextRes(@StringRes textRes: Int) = intent {
        friendActionTextResFetchJob?.cancel()
        friendActionTextResFetchJob = viewModelScope.launch {
            val uiState = state
            if (uiState !is UserProfileDialogUiState.Success) {
                return@launch
            }
            reduce { uiState.copy(friendActionFailureMessageRes = textRes) }
            delay(FriendActionMessageDurationMilli)
            reduce { uiState.copy(friendActionFailureMessageRes = null) }
        }
    }

    private fun requestFriend() = intent {
        val uiState = state
        check(uiState is UserProfileDialogUiState.Success)
        reduce { uiState.copy(isFriendActionLoading = true) }
        friendRepository.requestFriend(targetUserId = uiState.user.id)
            .onSuccess {
                reduce {
                    uiState.copy(
                        user = uiState.user.copy(friendStatus = FriendStatus.REQUESTED),
                        isFriendActionLoading = false
                    )
                }
                postSideEffect(
                    UserProfileDialogSideEffect.DidRequestFriend(user = uiState.user)
                )
                setFriendActionResultTextRes(R.string.success_to_request_friend)
            }.onFailure {
                setFriendActionErrorTextRes(R.string.failed_to_request_friend)
                reduce { uiState.copy(isFriendActionLoading = false) }
            }
    }

    private fun acceptFriendRequest() = intent {
        val uiState = state
        check(uiState is UserProfileDialogUiState.Success)
        reduce { uiState.copy(isFriendActionLoading = true) }
        friendRepository.acceptFriendRequest(requesterId = uiState.user.id)
            .onSuccess {
                reduce {
                    uiState.copy(
                        user = uiState.user.copy(friendStatus = FriendStatus.ACCEPTED),
                        isFriendActionLoading = false
                    )
                }
                postSideEffect(
                    UserProfileDialogSideEffect.DidAcceptFriend(user = uiState.user)
                )
                setFriendActionResultTextRes(R.string.accepted_friend_message)
            }.onFailure {
                setFriendActionErrorTextRes(R.string.failed_to_accept_friend_request)
                reduce { uiState.copy(isFriendActionLoading = false) }
            }
    }

    private fun cancelRequest() = intent {
        val uiState = state
        check(uiState is UserProfileDialogUiState.Success)
        reduce { uiState.copy(isFriendActionLoading = true) }
        friendRepository.deleteFriend(targetUserId = uiState.user.id)
            .onSuccess {
                reduce {
                    uiState.copy(
                        user = uiState.user.copy(friendStatus = FriendStatus.NONE),
                        isFriendActionLoading = false
                    )
                }
                postSideEffect(UserProfileDialogSideEffect.DidCancelRequest(user = uiState.user))
                setFriendActionResultTextRes(R.string.success_to_cancel_friend_request)
            }.onFailure {
                setFriendActionErrorTextRes(R.string.failed_to_cancel_friend_request)
                reduce { uiState.copy(isFriendActionLoading = false) }
            }
    }

    private fun cancelFriend() = intent {
        val uiState = state
        check(uiState is UserProfileDialogUiState.Success)
        reduce { uiState.copy(isFriendActionLoading = true) }
        friendRepository.deleteFriend(targetUserId = uiState.user.id)
            .onSuccess {
                reduce {
                    uiState.copy(
                        user = uiState.user.copy(friendStatus = FriendStatus.NONE),
                        isFriendActionLoading = false
                    )
                }
                postSideEffect(UserProfileDialogSideEffect.DidRemoveFriend(uiState.user))
                setFriendActionResultTextRes(R.string.success_to_cancel_friend)
            }.onFailure {
                setFriendActionErrorTextRes(R.string.failed_to_cancel_friend)
                reduce { uiState.copy(isFriendActionLoading = false) }
            }
    }

    companion object {
        private const val FriendActionMessageDurationMilli = 2000L
    }
}
