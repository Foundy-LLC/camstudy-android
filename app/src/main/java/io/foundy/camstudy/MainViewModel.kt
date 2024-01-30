package io.foundy.camstudy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.feature.auth.domain.model.AuthState
import io.foundy.feature.auth.domain.usecase.GetAuthStateStreamUseCase
import io.foundy.feature.auth.ui.destinations.LoginRouteDestination
import io.foundy.feature.home.ui.destinations.HomeRouteDestination
import io.foundy.feature.welcome.ui.destinations.WelcomeRouteDestination
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAuthStateStreamUseCase: GetAuthStateStreamUseCase,
) : ViewModel() {

    private var _startDestination: DirectionDestinationSpec? = null
    val startDestination: DirectionDestinationSpec? get() = _startDestination

    init {
        viewModelScope.launch {
            _startDestination = when (val authState = getAuthStateStreamUseCase().firstOrNull()) {
                is AuthState.SignedIn -> if (authState.existsInitInfo) {
                    HomeRouteDestination
                } else {
                    WelcomeRouteDestination
                }

                else -> LoginRouteDestination
            }
        }
    }
}
