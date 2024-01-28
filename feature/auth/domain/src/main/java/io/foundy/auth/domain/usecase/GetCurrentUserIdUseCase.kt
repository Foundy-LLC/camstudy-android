package io.foundy.auth.domain.usecase

import io.foundy.auth.domain.model.AuthState
import io.foundy.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String? {
        return when (val authState = authRepository.stateStream.firstOrNull()) {
            is AuthState.SignedIn -> authState.currentUserId
            else -> null
        }
    }
}
