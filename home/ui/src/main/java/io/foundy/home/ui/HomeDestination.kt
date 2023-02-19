package io.foundy.home.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.foundy.navigation.CamstudyDestination

object HomeDestination : CamstudyDestination {
    override val route: String = "home"
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.homeGraph() {
    composable(route = HomeDestination.route) {
        HomeRoute()
    }
}
