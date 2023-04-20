package io.foundy.crop.ui

import io.foundy.core.model.GrowingCrop

data class CropUiState(
    val growingCropUiState: GrowingCropUiState = GrowingCropUiState.Loading,
    val fetchGrowingCrop: () -> Unit
)

sealed class GrowingCropUiState {

    object Loading : GrowingCropUiState()

    data class Success(val growingCrop: GrowingCrop?) : GrowingCropUiState()

    data class Failure(val message: String?) : GrowingCropUiState()
}
