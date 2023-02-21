package io.foundy.home.ui.navigation

import androidx.annotation.StringRes
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.navigation.CamstudyDestination

data class HomeTabDestination(
    override val route: String,
    val selectedIcon: CamstudyIcon,
    val unselectedIcon: CamstudyIcon,
    @StringRes val iconTextId: Int,
) : CamstudyDestination
