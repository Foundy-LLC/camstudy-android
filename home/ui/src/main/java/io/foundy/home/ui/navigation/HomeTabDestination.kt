package io.foundy.home.ui.navigation

import androidx.annotation.StringRes
import com.ramcosta.composedestinations.spec.DestinationSpec
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.friend.ui.destinations.FriendRouteDestination
import io.foundy.home.ui.R
import io.foundy.room_list.ui.destinations.RoomListRouteDestination

enum class HomeTabDestination(
    val direction: DestinationSpec<*>,
    val selectedIcon: CamstudyIcon,
    val unselectedIcon: CamstudyIcon,
    @StringRes val label: Int
) {
    RoomList(
        direction = RoomListRouteDestination,
        selectedIcon = CamstudyIcons.Home,
        unselectedIcon = CamstudyIcons.HomeOutlined,
        label = R.string.home
    ),
    Friend(
        direction = FriendRouteDestination,
        selectedIcon = CamstudyIcons.People,
        unselectedIcon = CamstudyIcons.PeopleOutlined,
        label = R.string.friend
    )
}
