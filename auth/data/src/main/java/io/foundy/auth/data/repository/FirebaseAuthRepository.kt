package io.foundy.auth.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.foundy.core.common.di.ApplicationScope
import io.foundy.user.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    @ApplicationScope private val externalScope: CoroutineScope,
    private val userRepository: UserRepository
) : AuthRepository {

    private val _initializedState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val initializedStream: Flow<Boolean> get() = _initializedState

    private val _currentUserIdState: MutableStateFlow<String?> = MutableStateFlow(null)
    override val currentUserIdStream: Flow<String?> get() = _currentUserIdState

    private var _existsInitInfo: Boolean? = null
    override val existsInitInfo: Boolean? get() = _existsInitInfo

    init {
        Firebase.auth.addAuthStateListener { auth ->
            fetchState(auth.currentUser?.uid)
        }
    }

    private fun fetchState(currentUserId: String?) {
        if (currentUserId != null) {
            externalScope.launch {
                userRepository.getUserExistence(currentUserId).onSuccess {
                    _existsInitInfo = it
                }.onFailure {
                    // TODO: 에러처리
                }
                _initializedState.update { true }
                _currentUserIdState.update { currentUserId }
            }
        } else {
            _existsInitInfo = null
            _initializedState.update { true }
            _currentUserIdState.update { null }
        }
    }
}
