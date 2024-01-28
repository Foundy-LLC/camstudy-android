package io.foundy.home.ui.main

import androidx.annotation.StringRes
import io.foundy.home.ui.R

enum class MainTabDestination(@StringRes val labelRes: Int) {
    Dashboard(
        labelRes = R.string.dashboard
    ),
    StudyRooms(
        labelRes = R.string.study_room
    );

    companion object {
        val values = MainTabDestination.values()

        fun indexOf(destination: MainTabDestination): Int {
            return values.indexOf(destination)
        }
    }
}
