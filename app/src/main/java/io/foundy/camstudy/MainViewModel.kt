package io.foundy.camstudy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.auth.ui.destinations.LoginRouteDestination
import io.foundy.home.ui.destinations.HomeRouteDestination
import io.foundy.welcome.ui.destinations.WelcomeRouteDestination
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _startDestination: DirectionDestinationSpec? = null
    val startDestination: DirectionDestinationSpec? get() = _startDestination

    init {
        viewModelScope.launch {
            val currentUserId = authRepository.currentUserIdStream.first()
            if (currentUserId == null) {
                _startDestination = LoginRouteDestination
            } else {
                val existsInitInfo = authRepository.existsInitInfo
                if (existsInitInfo == null) {
                    // 로그인이 되어 있으나 초기 정보 입력 여부를 판단할 수 없는 경우 서버와의 연결에 실패한 것이다.
                    // 에러 메시지는 로그인 화면에서 보이기 때문에 따로 여기서 에러 메시지를 보이는 로직은 없다.
                    _startDestination = LoginRouteDestination
                    return@launch
                }
                _startDestination = if (existsInitInfo) {
                    HomeRouteDestination
                } else {
                    WelcomeRouteDestination
                }
            }
        }
    }
}
