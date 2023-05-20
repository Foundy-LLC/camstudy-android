package io.foundy.home.ui

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
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getUserUseCase: GetUserUseCase
) : ViewModel(), ContainerHost<HomeUiState, HomeSideEffect> {

    override val container: Container<HomeUiState, HomeSideEffect> = container(HomeUiState())

    init {
        viewModelScope.launch {
            val currentUserId = authRepository.currentUserIdStream.firstOrNull()
            check(currentUserId != null)
            getUserUseCase(userId = currentUserId)
                .onSuccess { user ->
                    intent {
                        reduce { state.copy(currentUserProfileImage = user.profileImage) }
                    }
                }.onFailure {
                    intent {
                        postSideEffect(
                            HomeSideEffect.Message(
                                content = it.message,
                                defaultRes = R.string.failed_to_load_my_profile_image
                            )
                        )
                    }
                }
        }
    }
}
