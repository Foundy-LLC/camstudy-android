package io.foundy.camstudy

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import dagger.hilt.android.AndroidEntryPoint
import io.foundy.auth.ui.destinations.LoginRouteDestination
import io.foundy.camstudy.ui.CamstudyApp
import io.foundy.home.ui.destinations.HomeRouteDestination
import io.foundy.welcome.ui.destinations.WelcomeRouteDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            navController = rememberAnimatedNavController()

            CamstudyApp(navController = navController)
        }
        showSplashUntilAuthIsReady()
        disableTransitionAnimation()
    }

    private fun showSplashUntilAuthIsReady() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val startDestination = viewModel.startDestination
                    return if (startDestination != null) {
                        if (startDestination !is LoginRouteDestination) {
                            navigateAndPopUpTo(startDestination)
                        }
                        lifecycleScope.launch {
                            // TODO: NavController에서 애니메이션 없이 전환하는 기능이 없기 때문에 임시적으로 첫 전환시에만
                            //  전환 애니메이션을 껐다가 다시 켜고 있다. 추후에 앞서 언급한 기능이 API에 추가되면 리팩토링 할 것
                            delay(1000)
                            enableTransitionAnimation()
                        }
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }

    private fun disableTransitionAnimation() {
        LoginRouteDestination.style = DestinationStyle.Animated.None
        WelcomeRouteDestination.style = DestinationStyle.Animated.None
        HomeRouteDestination.style = DestinationStyle.Animated.None
    }

    private fun enableTransitionAnimation() {
        LoginRouteDestination.style = DestinationStyle.Default
        WelcomeRouteDestination.style = DestinationStyle.Default
        HomeRouteDestination.style = DestinationStyle.Default
    }

    private fun navigateAndPopUpTo(destination: DirectionDestinationSpec) {
        navController.navigate(destination.route) {
            popUpTo(LoginRouteDestination.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}
