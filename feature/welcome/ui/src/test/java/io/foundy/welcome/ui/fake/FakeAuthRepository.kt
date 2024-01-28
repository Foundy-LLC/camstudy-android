package io.foundy.welcome.ui.fake

import io.foundy.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository : AuthRepository {
    override val currentUserIdStream: Flow<String?> = flowOf("id")
    override val existsInitInfo: Boolean = false
    override suspend fun markAsUserInitialInfoExists(userId: String) {
        TODO("Not yet implemented")
    }
}
