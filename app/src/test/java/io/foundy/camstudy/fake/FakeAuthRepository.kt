package io.foundy.camstudy.fake

import io.foundy.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeAuthRepository : AuthRepository {

    val currentUserIdSharedFlow = MutableSharedFlow<String?>(replay = 1)

    var existsInitInfoTestValue: Boolean? = false

    override val currentUserIdStream: Flow<String?> = currentUserIdSharedFlow

    override val existsInitInfo: Boolean?
        get() = existsInitInfoTestValue

    override suspend fun markAsUserInitialInfoExists(userId: String) {
        TODO("Not yet implemented")
    }
}
