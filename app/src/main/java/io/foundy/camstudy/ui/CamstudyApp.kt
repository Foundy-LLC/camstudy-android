package io.foundy.camstudy.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.scope.resultRecipient
import io.foundy.camstudy.navigation.RootNavGraph
import io.foundy.camstudy.navigation.navigator.LoginNavigatorImpl
import io.foundy.camstudy.navigation.navigator.WelcomeNavigatorImpl
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.crop.ui.destinations.PlantCropRouteDestination
import io.foundy.home.ui.HomeRoute
import io.foundy.home.ui.destinations.HomeRouteDestination

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun CamstudyApp(navController: NavHostController) {
    CamstudyTheme {
        DestinationsNavHost(
            navController = navController,
            engine = rememberAnimatedNavHostEngine(),
            navGraph = RootNavGraph,
            dependenciesContainerBuilder = {
                dependency(LoginNavigatorImpl(navController = navController))
                dependency(WelcomeNavigatorImpl(navController = navController))
            }
        ) {
            composable(HomeRouteDestination) {
                HomeRoute(
                    navigator = destinationsNavigator,
                    plantResultRecipient = resultRecipient<PlantCropRouteDestination, Boolean>()
                )
            }
        }
    }
}
