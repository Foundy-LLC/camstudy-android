package io.foundy.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    lateinit var currentUserId: String
        private set

    init {
        viewModelScope.launch {
            currentUserId = requireNotNull(authRepository.currentUserIdStream.first()) {
                "현재 로그인한 회원 정보가 없습니다. 로그인하지 않고 홈화면에 접속할 수 없습니다."
            }
        }
    }
}
