package io.foundy.feature.auth.domain.repository

import io.foundy.feature.auth.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val stateStream: Flow<AuthState>

    suspend fun markAsUserInitialInfoExists(userId: String)
}
