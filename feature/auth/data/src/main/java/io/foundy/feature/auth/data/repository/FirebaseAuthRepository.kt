package io.foundy.feature.auth.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import io.foundy.core.common.di.ApplicationScope
import io.foundy.feature.auth.data.source.AuthLocalDataSource
import io.foundy.feature.auth.data.source.AuthRemoteDataSource
import io.foundy.feature.auth.domain.model.AuthState
import io.foundy.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource
) : AuthRepository {

    override val stateStream: MutableSharedFlow<AuthState> = MutableSharedFlow(replay = 1)

    init {
        Firebase.auth.addAuthStateListener { auth ->
            fetchAndNotify(auth.currentUser?.uid)
        }
    }

    override suspend fun markAsUserInitialInfoExists(userId: String) {
        authLocalDataSource.markAsUserInitialInfoExists(userId = userId)
    }

    private fun fetchAndNotify(currentUserId: String?) {
        externalScope.launch {
            if (currentUserId == null) {
                stateStream.emit(AuthState.NotSignedIn)
                return@launch
            }
            val existsInLocal = authLocalDataSource.existsUserInitialInfo(currentUserId)
            if (existsInLocal) {
                stateStream.emit(
                    AuthState.SignedIn(
                        currentUserId = currentUserId,
                        existsInitInfo = true
                    )
                )
                return@launch
            }
            val response = authRemoteDataSource.getUserInitialInfoExistence(currentUserId)
            response.suspendOnSuccess {
                val exists = data.data
                if (exists) {
                    authLocalDataSource.markAsUserInitialInfoExists(currentUserId)
                }
                stateStream.emit(
                    AuthState.SignedIn(
                        currentUserId = currentUserId,
                        existsInitInfo = exists
                    )
                )
            }.suspendOnFailure {
                stateStream.emit(AuthState.Error)
            }
        }
    }
}
