package io.foundy.camstudy.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.foundy.camstudy.navigation.TopLevelDestination
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

    /**
     * UI logic for navigating to a particular destination in the app. The NavigationOptions to
     * navigate with are based on the type of destination, which could be a top level destination or
     * just a regular destination.
     *
     * Top level destinations have only one copy of the destination of the back stack, and save and
     * restore state whenever you navigate to and from it.
     * Regular destinations can have multiple copies in the back stack and state isn't saved nor
     * restored.
     *
     * @param destination The [CamstudyDestination] the app needs to navigate to.
     * @param route Optional route to navigate to in case the destination contains arguments.
     */
    fun navigate(destination: CamstudyDestination, route: String? = null) {
        if (destination is TopLevelDestination) {
            navController.navigate(route ?: destination.route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        } else {
            navController.navigate(route ?: destination.route)
        }
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
