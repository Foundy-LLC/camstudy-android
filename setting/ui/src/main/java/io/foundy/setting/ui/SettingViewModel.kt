package io.foundy.setting.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.ui.UserMessage
import io.foundy.user.domain.usecase.GetUserUseCase
import kotlinx.coroutines.flow.firstOrNull
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
    private val authRepository: AuthRepository,
    private val getUserUseCase: GetUserUseCase
) : ViewModel(), ContainerHost<SettingUiState, SettingSideEffect> {

    override val container: Container<SettingUiState, SettingSideEffect> =
        container(SettingUiState.Loading)

    private var _currentUserId: String? = null
    private val currentUserId: String get() = requireNotNull(_currentUserId)

    init {
        viewModelScope.launch {
            _currentUserId = authRepository.currentUserIdStream.firstOrNull()
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
}
