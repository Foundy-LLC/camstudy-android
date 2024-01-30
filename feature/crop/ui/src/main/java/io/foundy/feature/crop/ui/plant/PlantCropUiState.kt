package io.foundy.feature.crop.ui.plant

import io.foundy.core.model.CropType

data class PlantCropUiState(
    val selectedCropType: CropType? = null,
    val isPlanting: Boolean = false,
    val onSelected: (CropType) -> Unit,
    val onPlantClick: () -> Unit
) {
    val canPlant: Boolean get() = selectedCropType != null && !isPlanting
}
