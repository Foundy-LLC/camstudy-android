package io.foundy.welcome.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.foundy.navigation.CamstudyDestination

object WelcomeDestination : CamstudyDestination {
    override val route: String = "welcome"
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.welcomeGraph(onReplaceToHome: () -> Unit) {
    composable(route = WelcomeDestination.route) {
        WelcomeRoute(onReplaceToHome = onReplaceToHome)
    }
}
