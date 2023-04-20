package io.foundy.crop.ui.plant

import io.foundy.core.model.CropType

data class PlantCropUiState(
    val selectedCropType: CropType? = null,
    val onSelected: (CropType) -> Unit,
    val onPlantClick: () -> Unit
) {
    val canPlant: Boolean get() = selectedCropType != null
}
