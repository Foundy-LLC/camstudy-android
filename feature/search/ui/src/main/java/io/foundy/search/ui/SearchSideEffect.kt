package io.foundy.search.ui

import androidx.annotation.StringRes

sealed class SearchSideEffect {

    data class Message(
        val content: String? = null,
        @StringRes val defaultStringRes: Int
    ) : SearchSideEffect()
}
