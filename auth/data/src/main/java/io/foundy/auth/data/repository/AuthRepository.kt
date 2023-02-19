package io.foundy.auth.data.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val initializedStream: Flow<Boolean>
    val currentUserIdStream: Flow<String?>
    val existsInitInfo: Boolean?
}
