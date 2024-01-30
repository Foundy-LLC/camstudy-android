package io.foundy.feature.crop.ui.plant

sealed class PlantCropSideEffect {
    object SuccessToPlant : PlantCropSideEffect()
    data class FailedToPlant(val message: String?) : PlantCropSideEffect()
}
