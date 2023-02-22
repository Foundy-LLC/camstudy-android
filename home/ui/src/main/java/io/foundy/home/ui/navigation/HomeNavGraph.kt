package io.foundy.home.ui.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.spec.Route
import io.foundy.room_list.ui.destinations.RoomListRouteDestination

object HomeNavGraph : NavGraphSpec {

    override val route: String = "home"

    override val startRoute: Route = RoomListRouteDestination

    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
        RoomListRouteDestination,
    ).associateBy { it.route }
}
