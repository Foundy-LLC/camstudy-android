package io.foundy.welcome.ui

import androidx.annotation.StringRes

sealed class WelcomeSideEffect {
    object NavigateToHome : WelcomeSideEffect()
    data class Message(
        val content: String?,
        @StringRes val defaultContentRes: Int
    ) : WelcomeSideEffect()
}
