package io.foundy.home.ui.navigation

import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCallsBuilder
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import io.foundy.home.ui.HomeRoute
import io.foundy.home.ui.destinations.HomeRouteDestination

fun ManualComposableCallsBuilder.homeGraph() {
    composable(HomeRouteDestination) {
        HomeRoute()
    }
}
