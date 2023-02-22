package io.foundy.home.ui.navigation

import androidx.annotation.StringRes
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.icon.asCamstudyIcon
import io.foundy.home.ui.R
import io.foundy.room_list.ui.destinations.RoomListRouteDestination

enum class HomeTabDestination(
    val direction: DirectionDestinationSpec,
    val selectedIcon: CamstudyIcon,
    val unselectedIcon: CamstudyIcon,
    @StringRes val label: Int
) {
    RoomList(
        direction = RoomListRouteDestination,
        selectedIcon = CamstudyIcons.Home.asCamstudyIcon(),
        unselectedIcon = CamstudyIcons.HomeOutlined.asCamstudyIcon(),
        label = R.string.home
    ),
}
