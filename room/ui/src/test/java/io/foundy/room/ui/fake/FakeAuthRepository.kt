package io.foundy.room.ui.fake

import io.foundy.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAuthRepository(private val userId: String = "1234") : AuthRepository {

    override val currentUserIdStream: Flow<String?>
        get() = flow { emit(userId) }

    override val existsInitInfo: Boolean = true
}
