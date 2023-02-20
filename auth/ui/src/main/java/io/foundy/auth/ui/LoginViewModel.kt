package io.foundy.auth.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(), ContainerHost<LoginUiState, LoginSideEffect> {

    override val container = container<LoginUiState, LoginSideEffect>(LoginUiState)

    init {
        intent {
            authRepository.currentUserIdStream.collect { uid ->
                if (uid == null) {
                    return@collect
                }
                val existsInitInfo = authRepository.existsInitInfo
                if (existsInitInfo == null) {
                    postSideEffect(
                        LoginSideEffect.Message(
                            defaultMessageRes = R.string.failed_to_connect_server
                        )
                    )
                    return@collect
                }
                if (existsInitInfo) {
                    postSideEffect(LoginSideEffect.NavigateToHome)
                } else {
                    postSideEffect(LoginSideEffect.NavigateToWelcome)
                }
            }
        }
    }
}
