package io.foundy.auth.domain.usecase

import io.foundy.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ExistsInitInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean? {
        return authRepository.existsInitInfo
    }
}
