package io.foundy.auth.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.foundy.auth.data.source.AuthLocalDataSource
import io.foundy.auth.data.source.AuthRemoteDataSource
import io.foundy.core.common.di.ApplicationScope
import io.foundy.core.data.extension.getDataOrThrowMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
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

    private val _currentUserIdState: MutableSharedFlow<String?> = MutableSharedFlow(replay = 1)
    override val currentUserIdStream: Flow<String?> get() = _currentUserIdState

    private var _existsInitInfo: Boolean? = null
    override val existsInitInfo: Boolean? get() = _existsInitInfo

    init {
        Firebase.auth.addAuthStateListener { auth ->
            fetchAndNotify(auth.currentUser?.uid)
        }
    }

    private fun fetchAndNotify(currentUserId: String?) {
        externalScope.launch {
            if (currentUserId == null) {
                _existsInitInfo = null
                _currentUserIdState.emit(null)
                return@launch
            }
            val existsInLocal = authLocalDataSource.existsUserInitialInfo(currentUserId)
            if (existsInLocal) {
                _existsInitInfo = true
            } else {
                runCatching {
                    val response = authRemoteDataSource.getUserInitialInfoExistence(currentUserId)
                    response.getDataOrThrowMessage()
                }.onSuccess {
                    _existsInitInfo = it
                    authLocalDataSource.markAsUserInitialInfoExists(currentUserId)
                }
            }
            _currentUserIdState.emit(currentUserId)
        }
    }
}
