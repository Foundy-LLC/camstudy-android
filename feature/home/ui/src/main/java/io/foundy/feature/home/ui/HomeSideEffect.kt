package io.foundy.feature.home.ui

import androidx.annotation.StringRes

sealed class HomeSideEffect {
    data class Message(
        val content: String? = null,
        @StringRes val defaultRes: Int
    ) : HomeSideEffect()
}
