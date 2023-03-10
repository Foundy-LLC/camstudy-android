package io.foundy.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.foundy.home.ui.navigation.HomeTabDestination

@Composable
fun rememberHomeScreenState(
    navController: NavHostController = rememberNavController()
): HomeScreenState {
    return remember(navController) {
        HomeScreenState(navController)
    }
}

@Stable
class HomeScreenState(
    val navController: NavHostController,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    /**
     * Top level destinations to be used in the BottomBar and NavRail
     */
    val tabDestinations: List<HomeTabDestination> = HomeTabDestination.values().toList()

    fun navigate(destination: HomeTabDestination, route: String? = null) {
        navController.navigate(route ?: destination.direction.route) {
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
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}
