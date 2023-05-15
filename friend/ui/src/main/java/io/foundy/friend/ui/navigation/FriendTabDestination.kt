package io.foundy.friend.ui.navigation

import androidx.annotation.StringRes
import io.foundy.friend.ui.R

enum class FriendTabDestination(@StringRes val labelRes: Int) {
    List(
        labelRes = R.string.friend_tab_list
    ),
    Recommend(
        labelRes = R.string.friend_tab_recommend
    ),
    Requested(
        labelRes = R.string.friend_tab_requested
    );

    companion object {
        val values = FriendTabDestination.values()

        fun indexOf(destination: FriendTabDestination): Int {
            return values.indexOf(destination)
        }
    }
}
