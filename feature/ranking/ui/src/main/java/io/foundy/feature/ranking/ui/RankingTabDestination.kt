package io.foundy.feature.ranking.ui

import androidx.annotation.StringRes

enum class RankingTabDestination(@StringRes val labelRes: Int) {
    Total(
        labelRes = R.string.total_ranking_tab
    ),
    Weekly(
        labelRes = R.string.weekly_ranking_tab
    );

    companion object {
        val values = RankingTabDestination.values()

        fun indexOf(destination: RankingTabDestination): Int {
            return values.indexOf(destination)
        }
    }
}
