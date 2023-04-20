package io.foundy.crop.ui.plant

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.model.CropType
import io.foundy.crop.data.repository.CropRepository
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PlantCropViewModel @Inject constructor(
    private val cropRepository: CropRepository
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
        val cropType = state.selectedCropType
        check(cropType != null)
        reduce { state.copy(isPlanting = true) }
        cropRepository.plantCrop(cropType = cropType)
            .onSuccess {
                postSideEffect(PlantCropSideEffect.SuccessToPlant)
            }.onFailure {
                postSideEffect(PlantCropSideEffect.FailedToPlant(message = it.message))
            }
        reduce { state.copy(isPlanting = false) }
    }
}
