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
import io.foundy.feature.crop.ui.destinations.PlantCropRouteDestination
import io.foundy.feature.home.ui.HomeRoute
import io.foundy.feature.home.ui.destinations.HomeRouteDestination
import io.foundy.feature.setting.ui.SettingRoute
import io.foundy.feature.setting.ui.destinations.EditProfileRouteDestination
import io.foundy.feature.setting.ui.destinations.SettingRouteDestination
import io.foundy.feature.setting.ui.model.EditProfileResult

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
            composable(SettingRouteDestination) {
                val result = resultRecipient<EditProfileRouteDestination, EditProfileResult>()
                SettingRoute(
                    navigator = destinationsNavigator,
                    profileEditResultRecipient = result
                )
            }
        }
    }
}
