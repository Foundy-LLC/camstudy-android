package io.foundy.feature.setting.ui

import androidx.annotation.StringRes

sealed class SettingSideEffect {
    data class Message(
        val content: String? = null,
        @StringRes val defaultRes: Int
    ) : SettingSideEffect()
}
