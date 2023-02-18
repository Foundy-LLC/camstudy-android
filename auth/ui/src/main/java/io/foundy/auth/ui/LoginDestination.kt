package io.foundy.auth.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.foundy.navigation.CamstudyDestination

object LoginDestination : CamstudyDestination {
    override val route = "login"
}

fun NavGraphBuilder.loginGraph() {
    composable(route = LoginDestination.route) {
        LoginRoute()
    }
}
