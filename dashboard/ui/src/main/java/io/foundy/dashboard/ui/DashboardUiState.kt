package io.foundy.dashboard.ui

import io.foundy.core.model.GrowingCrop

data class DashboardUiState(
    val growingCropUiState: GrowingCropUiState = GrowingCropUiState.Loading
)

sealed class GrowingCropUiState {

    object Loading : GrowingCropUiState()

    data class Success(val growingCrop: GrowingCrop?) : GrowingCropUiState()

    data class Failure(val message: String?) : GrowingCropUiState()
}
