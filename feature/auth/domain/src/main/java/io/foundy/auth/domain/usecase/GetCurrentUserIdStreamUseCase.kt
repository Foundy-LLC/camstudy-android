package io.foundy.auth.domain.usecase

import io.foundy.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserIdStreamUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<String?> {
        return authRepository.currentUserIdStream
    }
}
