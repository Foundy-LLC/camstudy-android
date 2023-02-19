package io.foundy.camstudy

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import io.foundy.camstudy.ui.CamstudyApp
import io.foundy.camstudy.ui.CamstudyAppState
import io.foundy.camstudy.ui.rememberCamstudyAppState
import io.foundy.home.ui.HomeDestination
import io.foundy.navigation.CamstudyDestination
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavHostController
    private lateinit var appState: CamstudyAppState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navController = rememberAnimatedNavController()
            appState = rememberCamstudyAppState(navController = navController)

            CamstudyApp(appState = appState)
        }
        showSplashUntilAuthIsReady()
    }

    private fun showSplashUntilAuthIsReady() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel.shouldHideSplashScreen) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        if (viewModel.startDestination !is HomeDestination) {
                            navigateAndPopUpTo(viewModel.startDestination)
                        }
                        lifecycleScope.launch {
                            // TODO: NavController에서 애니메이션 없이 전환하는 기능이 없기 때문에 임시적으로 첫 전환시에만
                            //  전환 애니메이션을 껐다가 다시 켜고 있다. 추후에 앞서 언급한 기능이 API에 추가되면 리팩토링 할 것
                            appState.enableTransition()
                        }
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }

    private fun navigateAndPopUpTo(destination: CamstudyDestination) {
        navController.navigate(destination.route) {
            popUpTo(0)
        }
    }
}
