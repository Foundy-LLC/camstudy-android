package io.foundy.feature.auth.domain.usecase

import io.foundy.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class MarkAsUserInitialInfoExistsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String) {
        authRepository.markAsUserInitialInfoExists(userId = userId)
    }
}
