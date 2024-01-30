package io.foundy.camstudy.navigation.navigator

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import io.foundy.feature.auth.ui.LoginNavigator
import io.foundy.feature.auth.ui.destinations.LoginRouteDestination
import io.foundy.feature.home.ui.destinations.HomeRouteDestination
import io.foundy.feature.welcome.ui.destinations.WelcomeRouteDestination

internal class LoginNavigatorImpl(
    private val navController: NavController,
) : LoginNavigator {

    override fun replaceToHome() {
        navController.navigate(HomeRouteDestination) {
            popUpTo(LoginRouteDestination.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    override fun replaceToWelcome() {
        navController.navigate(WelcomeRouteDestination) {
            popUpTo(LoginRouteDestination.route) {
                inclusive = true
            }
        }
    }
}
