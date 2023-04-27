package io.foundy.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.crop.ui.CropRoute
import io.foundy.crop.ui.destinations.CropRouteDestination
import io.foundy.friend.ui.FriendRoute
import io.foundy.friend.ui.destinations.FriendRouteDestination
import io.foundy.home.ui.destinations.MainTabRouteDestination
import io.foundy.home.ui.main.MainTabRoute
import io.foundy.home.ui.navigation.HomeNavGraph
import io.foundy.home.ui.navigation.HomeTabDestination

@Destination(style = DestinationStyle.Runtime::class)
@Composable
fun HomeRoute(
    navigator: DestinationsNavigator,
    plantResultRecipient: OpenResultRecipient<Boolean>,
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeScreen(
        navigator = navigator,
        plantResultRecipient = plantResultRecipient,
        currentUserId = viewModel.currentUserId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    currentUserId: String,
    plantResultRecipient: OpenResultRecipient<Boolean>,
    homeScreenState: HomeScreenState = rememberHomeScreenState(),
) {
    Scaffold(
        topBar = {
            CamstudyTopAppBar(
                title = {
                    CamstudyIcon(
                        icon = CamstudyIcons.AppTitle,
                        tint = Color.Unspecified,
                        contentDescription = null,
                    )
                }
            )
        },
        bottomBar = {
            CamstudyNavigationBar(
                destinations = homeScreenState.tabDestinations,
                onNavigateToDestination = homeScreenState::navigate,
                currentDestination = homeScreenState.currentDestination
            )
        },
        snackbarHost = { SnackbarHost(hostState = homeScreenState.snackbarHostState) }
    ) { padding ->
        DestinationsNavHost(
            navGraph = HomeNavGraph,
            navController = homeScreenState.navController,
            modifier = Modifier.padding(padding),
        ) {
            composable(MainTabRouteDestination) {
                MainTabRoute(
                    navigator = navigator,
                    plantResultRecipient = plantResultRecipient,
                    navigateToCropTab = { homeScreenState.navigate(HomeTabDestination.Crop) },
                    navigateToRankingTab = { homeScreenState.navigate(HomeTabDestination.Ranking) },
                    showSnackbar = homeScreenState::showSnackbar
                )
            }
            composable(CropRouteDestination) {
                CropRoute(
                    navigator = navigator,
                    plantResultRecipient = plantResultRecipient,
                    showSnackbar = homeScreenState::showSnackbar
                )
            }
            composable(FriendRouteDestination) {
                FriendRoute(userId = currentUserId)
            }
        }
    }
}

@Composable
private fun CamstudyNavigationBar(
    destinations: List<HomeTabDestination>,
    onNavigateToDestination: (HomeTabDestination) -> Unit,
    currentDestination: NavDestination?
) {
    Box {
        NavigationBar(
            modifier = Modifier.height(60.dp),
            containerColor = CamstudyTheme.colorScheme.systemBackground
        ) {
            for (destination in destinations) {
                val selected = currentDestination?.hierarchy?.any {
                    it.route == destination.direction.route
                } == true

                CamstudyNavigationBarItem(
                    selected = selected,
                    icon = destination.icon,
                    label = stringResource(id = destination.label),
                    onClick = { onNavigateToDestination(destination) },
                )
            }
        }
        CamstudyDivider()
    }
}

@Composable
fun RowScope.CamstudyNavigationBarItem(
    selected: Boolean,
    icon: CamstudyIcon,
    label: String,
    onClick: () -> Unit,
) {
    val color = if (selected) {
        CamstudyTheme.colorScheme.primary
    } else {
        CamstudyTheme.colorScheme.systemUi03
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CamstudyIcon(
            modifier = Modifier.size(24.dp),
            icon = icon,
            tint = color,
            contentDescription = label
        )
        Box(modifier = Modifier.height(4.dp))
        CamstudyText(
            text = label,
            style = CamstudyTheme.typography.labelMedium.copy(color = color)
        )
    }
}

@Preview
@Composable
private fun CamstudyNavigationBarPreview() {
    CamstudyTheme {
        CamstudyNavigationBar(
            destinations = HomeTabDestination.values().toList(),
            onNavigateToDestination = {},
            currentDestination = NavDestination(navigatorName = HomeTabDestination.Main.name)
        )
    }
}
