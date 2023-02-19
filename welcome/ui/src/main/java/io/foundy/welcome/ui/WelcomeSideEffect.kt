package io.foundy.welcome.ui

sealed class WelcomeSideEffect {
    object NavigateToHome : WelcomeSideEffect()
}
