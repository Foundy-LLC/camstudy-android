package io.foundy.home.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.home.ui.navigation.HomeNavGraph
import io.foundy.home.ui.navigation.HomeTabDestination
import io.foundy.room_list.ui.RoomListRoute
import io.foundy.room_list.ui.destinations.RoomListRouteDestination

@Destination(style = DestinationStyle.Runtime::class)
@Composable
fun HomeRoute(
    navigator: DestinationsNavigator
) {
    HomeScreen(
        rootNavigator = navigator,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeScreenState: HomeScreenState = rememberHomeScreenState(),
    rootNavigator: DestinationsNavigator,
) {
    Scaffold(
        bottomBar = {
            CamstudyBottomBar(
                destinations = homeScreenState.tabDestinations,
                onNavigateToDestination = homeScreenState::navigate,
                currentDestination = homeScreenState.currentDestination
            )
        }
    ) { padding ->
        DestinationsNavHost(
            navGraph = HomeNavGraph,
            navController = homeScreenState.navController,
            modifier = Modifier.padding(padding),
        ) {
            composable(RoomListRouteDestination) {
                RoomListRoute(parentNavigator = rootNavigator)
            }
        }
    }
}

@Composable
private fun CamstudyBottomBar(
    destinations: List<HomeTabDestination>,
    onNavigateToDestination: (HomeTabDestination) -> Unit,
    currentDestination: NavDestination?
) {
    NavigationBar {
        for (destination in destinations) {
            val selected = currentDestination?.hierarchy?.any {
                it.route == destination.direction.route
            } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }
                    CamstudyIcon(
                        icon = icon,
                        contentDescription = stringResource(id = destination.label)
                    )
                },
                label = { Text(stringResource(destination.label)) }
            )
        }
    }
}
