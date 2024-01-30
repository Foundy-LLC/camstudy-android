package io.foundy.feature.auth.domain.usecase

import io.foundy.feature.auth.domain.model.AuthState
import io.foundy.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthStateStreamUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthState> {
        return authRepository.stateStream
    }
}
