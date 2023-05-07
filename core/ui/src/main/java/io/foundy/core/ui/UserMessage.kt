package io.foundy.core.ui

import androidx.annotation.StringRes

data class UserMessage(
    val content: String? = null,
    @StringRes val defaultRes: Int
)
