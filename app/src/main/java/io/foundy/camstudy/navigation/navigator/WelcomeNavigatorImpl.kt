package io.foundy.camstudy.navigation.navigator

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import io.foundy.feature.home.ui.destinations.HomeRouteDestination
import io.foundy.feature.welcome.ui.WelcomeNavigator
import io.foundy.feature.welcome.ui.destinations.WelcomeRouteDestination

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
