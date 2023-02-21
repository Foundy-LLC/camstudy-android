package io.foundy.camstudy.navigation.navigator

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import io.foundy.auth.ui.LoginNavigator
import io.foundy.auth.ui.destinations.LoginRouteDestination
import io.foundy.home.ui.destinations.HomeRouteDestination
import io.foundy.welcome.ui.destinations.WelcomeRouteDestination

class LoginNavigatorImpl(
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
