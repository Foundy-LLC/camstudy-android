package io.foundy.feature.crop.ui

import androidx.annotation.StringRes

sealed class CropSideEffect {

    data class Message(
        val content: String? = null,
        @StringRes val defaultRes: Int
    ) : CropSideEffect()

    object NavigateToPlantScreen : CropSideEffect()
}
