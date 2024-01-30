package io.foundy.feature.home.ui.navigation

import androidx.annotation.StringRes
import com.ramcosta.composedestinations.spec.DestinationSpec
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.feature.crop.ui.destinations.CropRouteDestination
import io.foundy.feature.friend.ui.destinations.FriendRouteDestination
import io.foundy.feature.home.ui.R
import io.foundy.feature.home.ui.destinations.MainTabRouteDestination
import io.foundy.feature.ranking.ui.destinations.RankingRouteDestination

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
    Ranking(
        direction = RankingRouteDestination,
        icon = CamstudyIcons.Ranking,
        label = R.string.ranking
    ),
    Friend(
        direction = FriendRouteDestination,
        icon = CamstudyIcons.People,
        label = R.string.friend
    )
}
