package io.foundy.home.ui.navigation

import androidx.annotation.StringRes
import com.ramcosta.composedestinations.spec.DestinationSpec
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.crop.ui.destinations.CropRouteDestination
import io.foundy.friend.ui.destinations.FriendRouteDestination
import io.foundy.home.ui.R
import io.foundy.home.ui.destinations.MainTabRouteDestination
import io.foundy.search.ui.destinations.SearchRouteDestination

enum class HomeTabDestination(
    val direction: DestinationSpec<*>,
    val icon: CamstudyIcon,
    @StringRes val label: Int
) {
    Main(
        direction = MainTabRouteDestination,
        icon = CamstudyIcons.Home,
        label = R.string.main
    ),
    Crop(
        direction = CropRouteDestination,
        icon = CamstudyIcons.Crop,
        label = R.string.my_crop
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
