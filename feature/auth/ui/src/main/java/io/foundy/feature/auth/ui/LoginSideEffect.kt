package io.foundy.feature.auth.ui

import androidx.annotation.StringRes

sealed class LoginSideEffect {
    object NavigateToHome : LoginSideEffect()
    object NavigateToWelcome : LoginSideEffect()
    data class Message(
        val message: String? = null,
        @StringRes val defaultMessageRes: Int
    ) : LoginSideEffect()
}
