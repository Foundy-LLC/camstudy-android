package io.foundy.feature.auth.domain.model

sealed class AuthState {
    object NotSignedIn : AuthState()
    data class SignedIn(val currentUserId: String, val existsInitInfo: Boolean) : AuthState()
    object Error : AuthState() // TODO: Add error context message
}
