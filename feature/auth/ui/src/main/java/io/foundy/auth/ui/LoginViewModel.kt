package io.foundy.auth.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.domain.model.AuthState
import io.foundy.auth.domain.usecase.GetAuthStateStreamUseCase
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getAuthStateStreamUseCase: GetAuthStateStreamUseCase,
) : ViewModel(), ContainerHost<LoginUiState, LoginSideEffect> {

    override val container = container<LoginUiState, LoginSideEffect>(LoginUiState())

    init {
        intent {
            getAuthStateStreamUseCase().collect { authState ->
                when (authState) {
                    AuthState.Error -> postSideEffect(
                        LoginSideEffect.Message(
                            defaultMessageRes = R.string.failed_to_connect_server
                        )
                    )

                    is AuthState.SignedIn -> if (authState.existsInitInfo) {
                        postSideEffect(LoginSideEffect.NavigateToHome)
                    } else {
                        postSideEffect(LoginSideEffect.NavigateToWelcome)
                    }

                    AuthState.NotSignedIn -> {}
                }
            }
        }
    }

    fun setInProgressGoogleSignIn(inProgress: Boolean) = intent {
        reduce {
            state.copy(inProgressGoogleSignIn = inProgress)
        }
    }
}
