package io.foundy.camstudy.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.foundy.navigation.CamstudyDestination
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberCamstudyAppState(
    navController: NavHostController = rememberAnimatedNavController()
): CamstudyAppState {
    return remember(navController) {
        CamstudyAppState(navController)
    }
}

@Stable
class CamstudyAppState(
    val navController: NavHostController,
) {
    var enabledTransition by mutableStateOf(false)
        private set

    suspend fun enableTransitionAfterDelay() {
        delay(CamstudyTransitions.DurationMilli.toLong())
        enabledTransition = true
    }

    fun navigate(destination: CamstudyDestination, route: String? = null) {
        navController.navigate(route ?: destination.route)
    }

    fun popUpAndNavigate(
        destination: CamstudyDestination,
        popUpToDestination: CamstudyDestination,
        route: String? = null
    ) {
        navController.navigate(route ?: destination.route) {
            popUpTo(popUpToDestination.route) {
                inclusive = true
            }
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}

object CamstudyTransitions {
    const val DurationMilli = 300
}
