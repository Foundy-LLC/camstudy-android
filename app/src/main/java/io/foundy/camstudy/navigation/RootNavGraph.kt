package io.foundy.camstudy.navigation

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.spec.Route
import io.foundy.feature.auth.ui.destinations.LoginRouteDestination
import io.foundy.feature.crop.ui.destinations.PlantCropRouteDestination
import io.foundy.feature.home.ui.destinations.HomeRouteDestination
import io.foundy.feature.home.ui.navigation.HomeNavGraph
import io.foundy.feature.room_list.ui.create.destinations.RoomCreateScreenDestination
import io.foundy.feature.search.ui.destinations.SearchRouteDestination
import io.foundy.feature.setting.ui.destinations.EditProfileRouteDestination
import io.foundy.feature.setting.ui.destinations.OrganizationEditRouteDestination
import io.foundy.feature.setting.ui.destinations.SettingRouteDestination
import io.foundy.feature.welcome.ui.destinations.WelcomeRouteDestination

internal object RootNavGraph : NavGraphSpec {

    override val route: String = "root"

    override val startRoute: Route = StartDestination

    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
        HomeRouteDestination,
        LoginRouteDestination,
        WelcomeRouteDestination,
        PlantCropRouteDestination,
        RoomCreateScreenDestination,
        SearchRouteDestination,
        SettingRouteDestination,
        EditProfileRouteDestination,
        OrganizationEditRouteDestination
    ).associateBy { it.route }

    override val nestedNavGraphs: List<NavGraphSpec> = listOf(
        HomeNavGraph
    )
}
