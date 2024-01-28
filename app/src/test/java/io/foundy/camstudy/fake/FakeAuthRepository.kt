package io.foundy.camstudy.fake

import io.foundy.auth.domain.model.AuthState
import io.foundy.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeAuthRepository : AuthRepository {

    override val stateStream: MutableSharedFlow<AuthState> = MutableSharedFlow(replay = 1)

    suspend fun emitState(newState: AuthState) {
        stateStream.emit(newState)
    }

    override suspend fun markAsUserInitialInfoExists(userId: String) {
        TODO("Not yet implemented")
    }
}
