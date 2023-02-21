package io.foundy.home.ui.navigation

import io.foundy.room_list.ui.RoomListNavigator

internal class RoomListNavigatorImpl(
    private val onNavigateToRoom: (id: String) -> Unit
) : RoomListNavigator {

    override fun navigateToRoom(id: String) {
        onNavigateToRoom(id)
    }
}
