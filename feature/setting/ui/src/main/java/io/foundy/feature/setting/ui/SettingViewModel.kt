package io.foundy.feature.setting.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.ui.UserMessage
import io.foundy.feature.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.feature.setting.ui.model.EditProfileResult
import io.foundy.feature.user.domain.usecase.GetUserUseCase
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel(), ContainerHost<SettingUiState, SettingSideEffect> {

    override val container: Container<SettingUiState, SettingSideEffect> =
        container(SettingUiState.Loading)

    private var _currentUserId: String? = null
    private val currentUserId: String get() = requireNotNull(_currentUserId)

    init {
        viewModelScope.launch {
            _currentUserId = getCurrentUserIdUseCase()
            check(_currentUserId != null)
            fetchCurrentUser()
        }
    }

    private fun fetchCurrentUser() = intent {
        reduce { SettingUiState.Loading }
        getUserUseCase(userId = currentUserId)
            .onSuccess { user ->
                reduce { SettingUiState.Success(currentUser = user) }
            }.onFailure {
                val message = UserMessage(
                    content = it.message,
                    defaultRes = R.string.failed_to_load_my_profile
                )
                postSideEffect(
                    SettingSideEffect.Message(
                        content = message.content,
                        defaultRes = message.defaultRes
                    )
                )
                reduce { SettingUiState.Failure(message = message) }
            }
    }

    fun updateProfile(editProfileResult: EditProfileResult) = intent {
        val uiState = state
        if (uiState !is SettingUiState.Success) {
            return@intent
        }
        reduce {
            uiState.copy(
                currentUser = uiState.currentUser.copy(
                    name = editProfileResult.name,
                    introduce = editProfileResult.introduce,
                    profileImage = editProfileResult.profileImage,
                    tags = editProfileResult.tags
                )
            )
        }
    }
}
