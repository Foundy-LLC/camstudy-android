package io.foundy.feature.room.ui.fake

import io.foundy.feature.auth.domain.model.AuthState
import io.foundy.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthRepository(private val userId: String = "1234") : AuthRepository {

    override val stateStream: Flow<AuthState>
        get() = flow { emit(AuthState.SignedIn(currentUserId = userId, existsInitInfo = true)) }

    override suspend fun markAsUserInitialInfoExists(userId: String) {
        TODO("Not yet implemented")
    }
}
