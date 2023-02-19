package io.foundy.auth.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.foundy.navigation.CamstudyDestination

object LoginDestination : CamstudyDestination {
    override val route = "login"
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.loginGraph(onReplaceToHome: () -> Unit, onReplaceToWelcome: () -> Unit) {
    composable(route = LoginDestination.route) {
        LoginRoute(onReplaceToHome = onReplaceToHome, onReplaceToWelcome = onReplaceToWelcome)
    }
}
