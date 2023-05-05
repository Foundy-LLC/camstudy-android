package io.foundy.crop.ui

import androidx.annotation.StringRes

sealed class CropSideEffect {

    data class Message(
        val content: String? = null,
        @StringRes val defaultRes: Int
    ) : CropSideEffect()
}
