package io.foundy.camstudy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.auth.ui.LoginDestination
import io.foundy.home.ui.HomeDestination
import io.foundy.navigation.CamstudyDestination
import io.foundy.welcome.ui.WelcomeDestination
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _shouldHideSplashScreen: Boolean = false
    val shouldHideSplashScreen: Boolean get() = _shouldHideSplashScreen

    private var _startDestination: CamstudyDestination = HomeDestination
    val startDestination: CamstudyDestination get() = _startDestination

    init {
        viewModelScope.launch {
            authRepository.initializedStream.collectLatest { initialized ->
                if (initialized) {
                    val currentUserId = authRepository.currentUserIdStream.first()

                    _startDestination = if (currentUserId == null) {
                        LoginDestination
                    } else {
                        val existsInitInfo = authRepository.existsInitInfo
                        // TODO: null인경우 while문으로 반복 체크하기
                        check(existsInitInfo != null)
                        if (existsInitInfo) {
                            HomeDestination
                        } else {
                            WelcomeDestination
                        }
                    }
                    _shouldHideSplashScreen = true
                    cancel()
                }
            }
        }
    }
}
