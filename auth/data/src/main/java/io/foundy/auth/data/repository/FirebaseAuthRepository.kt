package io.foundy.auth.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.foundy.auth.data.source.AuthLocalDataSource
import io.foundy.auth.data.source.AuthRemoteDataSource
import io.foundy.core.common.di.ApplicationScope
import io.foundy.core.data.extension.getDataOrThrowMessage
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

    override val currentUserIdStream: MutableSharedFlow<String?> = MutableSharedFlow(replay = 1)
    override var existsInitInfo: Boolean? = null

    init {
        Firebase.auth.addAuthStateListener { auth ->
            fetchAndNotify(auth.currentUser?.uid)
        }
    }

    private fun fetchAndNotify(currentUserId: String?) {
        externalScope.launch {
            if (currentUserId == null) {
                existsInitInfo = null
                currentUserIdStream.emit(null)
                return@launch
            }
            val existsInLocal = authLocalDataSource.existsUserInitialInfo(currentUserId)
            if (existsInLocal) {
                existsInitInfo = true
            } else {
                runCatching {
                    val response = authRemoteDataSource.getUserInitialInfoExistence(currentUserId)
                    response.getDataOrThrowMessage()
                }.onSuccess {exists ->
                    existsInitInfo = exists
                    if (exists) {
                        authLocalDataSource.markAsUserInitialInfoExists(currentUserId)
                    }
                }
            }
            currentUserIdStream.emit(currentUserId)
        }
    }
}
