package io.foundy.home.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import io.foundy.core.designsystem.icon.DrawableResourceIcon
import io.foundy.core.designsystem.icon.ImageVectorIcon
import io.foundy.home.ui.navigation.HomeNavHost
import io.foundy.home.ui.navigation.HomeTabDestination

@Composable
fun HomeRoute() {
    HomeScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeScreenState: HomeScreenState = rememberHomeScreenState()
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
        HomeNavHost(
            modifier = Modifier.padding(padding),
            navController = homeScreenState.navController
        )
    }
}

@Composable
private fun CamstudyBottomBar(
    destinations: List<HomeTabDestination>,
    onNavigateToDestination: (HomeTabDestination) -> Unit,
    currentDestination: NavDestination?
) {
    NavigationBar {
        destinations.forEach { destination ->
            val selected =
                currentDestination?.hierarchy?.any { it.route == destination.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }
                    when (icon) {
                        is ImageVectorIcon -> Icon(
                            imageVector = icon.imageVector,
                            contentDescription = stringResource(id = destination.iconTextId)
                        )
                        is DrawableResourceIcon -> Icon(
                            painter = painterResource(id = icon.id),
                            contentDescription = stringResource(id = destination.iconTextId)
                        )
                    }
                },
                label = { Text(stringResource(destination.iconTextId)) }
            )
        }
    }
}
