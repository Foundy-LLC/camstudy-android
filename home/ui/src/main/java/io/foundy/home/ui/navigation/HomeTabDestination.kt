package io.foundy.home.ui.navigation

import androidx.annotation.StringRes
import com.ramcosta.composedestinations.spec.DestinationSpec
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.friend.ui.destinations.FriendRouteDestination
import io.foundy.home.ui.R
import io.foundy.room_list.ui.destinations.RoomListRouteDestination
import io.foundy.search.ui.destinations.SearchRouteDestination

enum class HomeTabDestination(
    val direction: DestinationSpec<*>,
    val icon: CamstudyIcon,
    @StringRes val label: Int
) {
    RoomList(
        direction = RoomListRouteDestination,
        icon = CamstudyIcons.StudyRoom,
        label = R.string.study_room
    ),
    Search(
        direction = SearchRouteDestination,
        icon = CamstudyIcons.Search,
        label = R.string.search
    ),
    Friend(
        direction = FriendRouteDestination,
        icon = CamstudyIcons.People,
        label = R.string.friend
    )
}
