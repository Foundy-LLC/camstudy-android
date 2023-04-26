package io.foundy.home.ui.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.spec.Route
import io.foundy.home.ui.destinations.MainTabRouteDestination

object HomeNavGraph : NavGraphSpec {

    override val route: String = "home"

    override val startRoute: Route = MainTabRouteDestination

    override val destinationsByRoute: Map<String, DestinationSpec<*>> = HomeTabDestination.values()
        .map { it.direction }
        .toList()
        .associateBy { it.route }
}
