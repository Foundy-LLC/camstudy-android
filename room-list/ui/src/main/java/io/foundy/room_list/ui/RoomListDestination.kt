package io.foundy.room_list.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.foundy.navigation.CamstudyDestination

object RoomListDestination : CamstudyDestination {
    override val route: String = "room_list"
}

fun NavGraphBuilder.roomListGraph() {
    composable(route = RoomListDestination.route) {
        RoomListRoute()
    }
}
