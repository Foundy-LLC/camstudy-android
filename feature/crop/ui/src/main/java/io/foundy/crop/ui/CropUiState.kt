package io.foundy.crop.ui

import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop

data class CropUiState(
    val growingCropUiState: GrowingCropUiState = GrowingCropUiState.Loading,
    val harvestedCropsUiState: HarvestedCropsUiState = HarvestedCropsUiState.Loading,
    val fetchGrowingCrop: () -> Unit,
    val onRefreshing: () -> Unit
) {
    val isRefreshing: Boolean
        get() {
            return growingCropUiState is GrowingCropUiState.Loading &&
                harvestedCropsUiState is HarvestedCropsUiState.Loading
        }
}

sealed class GrowingCropUiState {

    object Loading : GrowingCropUiState()

    data class Success(
        val growingCrop: GrowingCrop?,
        val onHarvestClick: (GrowingCrop) -> Unit,
        val onReplantClick: (GrowingCrop) -> Unit,
        val onRemoveClick: (GrowingCrop) -> Unit,
        val isInHarvesting: Boolean = false,
        val isInDeleting: Boolean = false
    ) : GrowingCropUiState()

    data class Failure(val message: String?) : GrowingCropUiState()
}

sealed class HarvestedCropsUiState {

    object Loading : HarvestedCropsUiState()

    data class Success(val harvestedCrops: List<HarvestedCrop>) : HarvestedCropsUiState()

    data class Failure(val message: String?) : HarvestedCropsUiState()
}
