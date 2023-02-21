package io.foundy.home.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.foundy.room_list.ui.RoomListDestination
import io.foundy.room_list.ui.roomListGraph

@Composable
fun HomeNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = RoomListDestination.route,
    ) {
        roomListGraph()
    }
}
