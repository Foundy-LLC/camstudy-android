package io.foundy.camstudy.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.spec.Route
import io.foundy.auth.ui.destinations.LoginRouteDestination
import io.foundy.home.ui.destinations.HomeRouteDestination
import io.foundy.welcome.ui.destinations.WelcomeRouteDestination

internal object NavGraphs {

    val root = object : NavGraphSpec {
        override val route: String = "root"
        override val startRoute: Route = LoginRouteDestination
        override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
            HomeRouteDestination,
            LoginRouteDestination,
            WelcomeRouteDestination,
        ).associateBy { it.route }
    }
}
