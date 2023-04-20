package io.foundy.crop.ui.plant

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.model.CropType
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PlantCropViewModel @Inject constructor(
    // TODO
) : ViewModel(), ContainerHost<PlantCropUiState, PlantCropSideEffect> {

    override val container: Container<PlantCropUiState, PlantCropSideEffect> = container(
        PlantCropUiState(
            onSelected = ::updateSelectedCropType,
            onPlantClick = ::plantCrop
        )
    )

    private fun updateSelectedCropType(cropType: CropType) = intent {
        reduce { state.copy(selectedCropType = cropType) }
    }

    private fun plantCrop() = intent {
        // TODO
    }
}
