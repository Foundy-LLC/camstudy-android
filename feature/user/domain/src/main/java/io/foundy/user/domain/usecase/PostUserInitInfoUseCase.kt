package io.foundy.user.domain.usecase

import io.foundy.user.domain.repository.UserRepository
import java.io.File
import javax.inject.Inject

class PostUserInitInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        name: String,
        introduce: String?,
        tags: List<String>,
        profileImage: File?
    ): Result<Unit> {
        return userRepository.postUserInitialInfo(
            userId = userId,
            name = name,
            introduce = introduce,
            tags = tags,
            profileImage = profileImage
        )
    }
}
