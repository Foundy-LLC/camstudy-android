package io.foundy.setting.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
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
        container(SettingUiState())

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
        reduce { state.copy(isLoading = true) }
        getUserUseCase(userId = currentUserId)
            .onSuccess { user ->
                reduce { state.copy(currentUser = user) }
            }.onFailure {
                intent {
                    postSideEffect(
                        SettingSideEffect.Message(
                            content = it.message,
                            defaultRes = R.string.failed_to_load_my_profile
                        )
                    )
                }
            }
        reduce { state.copy(isLoading = false) }
    }
}
