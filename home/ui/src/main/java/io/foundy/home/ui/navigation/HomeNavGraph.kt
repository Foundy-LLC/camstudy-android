package io.foundy.home.ui.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.spec.Route
import io.foundy.friend.ui.destinations.FriendRouteDestination
import io.foundy.home.ui.destinations.MainTabRouteDestination
import io.foundy.search.ui.destinations.SearchRouteDestination

object HomeNavGraph : NavGraphSpec {

    override val route: String = "home"

    override val startRoute: Route = MainTabRouteDestination

    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
        MainTabRouteDestination,
        SearchRouteDestination,
        FriendRouteDestination
    ).associateBy { it.route }
}
