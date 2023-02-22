package io.foundy.camstudy.navigation.navigator

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import io.foundy.home.ui.destinations.HomeRouteDestination
import io.foundy.welcome.ui.WelcomeNavigator
import io.foundy.welcome.ui.destinations.WelcomeRouteDestination

internal class WelcomeNavigatorImpl(
    private val navController: NavController,
) : WelcomeNavigator {

    override fun replaceToHome() {
        navController.navigate(HomeRouteDestination) {
            popUpTo(WelcomeRouteDestination.route) {
                inclusive = true
            }
        }
    }
}
