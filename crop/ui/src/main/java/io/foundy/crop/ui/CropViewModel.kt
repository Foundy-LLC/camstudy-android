package io.foundy.crop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.model.GrowingCrop
import io.foundy.crop.data.repository.CropRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cropRepository: CropRepository
) : ViewModel(), ContainerHost<CropUiState, CropSideEffect> {

    override val container: Container<CropUiState, CropSideEffect> = container(
        CropUiState(
            fetchGrowingCrop = ::fetchGrowingCrop,
            onRefreshing = ::refresh
        )
    )

    private lateinit var currentUserId: String

    init {
        viewModelScope.launch {
            currentUserId = requireNotNull(authRepository.currentUserIdStream.firstOrNull()) {
                "현재 로그인한 회원 아이디를 얻을 수 없습니다. 로그인 하지 않고 작물을 보려 했습니다."
            }
            refresh()
        }
        viewModelScope.launch {
            cropRepository.currentUserGrowingCropFlow.collectLatest { growingCrop ->
                intent {
                    reduce {
                        state.copy(
                            growingCropUiState = GrowingCropUiState.Success(
                                growingCrop = growingCrop,
                                onHarvestClick = ::harvestGrowingCrop,
                                onReplantClick = ::deleteGrowingCrop
                            )
                        )
                    }
                }
            }
        }
    }

    private fun refresh() = intent {
        fetchGrowingCrop()
        fetchHarvestedCrops()
    }

    private fun fetchGrowingCrop() = intent {
        cropRepository.getGrowingCrop(userId = currentUserId)
            .onFailure { throwable ->
                reduce {
                    state.copy(
                        growingCropUiState = GrowingCropUiState.Failure(throwable.message)
                    )
                }
            }
    }

    private fun fetchHarvestedCrops() = intent {
        cropRepository.getHarvestedCrops(userId = currentUserId)
            .onSuccess { crops ->
                reduce {
                    state.copy(
                        harvestedCropsUiState = HarvestedCropsUiState.Success(
                            harvestedCrops = crops
                        )
                    )
                }
            }.onFailure {
                reduce {
                    state.copy(
                        harvestedCropsUiState = HarvestedCropsUiState.Failure(message = it.message)
                    )
                }
            }
    }

    private fun harvestGrowingCrop(growingCrop: GrowingCrop) = intent {
        val growingCropUiState = state.growingCropUiState
        check(growingCropUiState is GrowingCropUiState.Success)
        reduce { state.copy(growingCropUiState = growingCropUiState.copy(isInHarvesting = true)) }
        cropRepository.harvestCrop(cropId = growingCrop.id)
            .onSuccess {
                fetchHarvestedCrops()
                reduce {
                    state.copy(
                        growingCropUiState = growingCropUiState.copy(
                            growingCrop = null,
                            isInHarvesting = false
                        )
                    )
                }
                postSideEffect(
                    CropSideEffect.Message(
                        defaultRes = R.string.success_to_harvest_crop
                    )
                )
            }.onFailure {
                postSideEffect(
                    CropSideEffect.Message(
                        content = it.message,
                        defaultRes = R.string.failed_to_harvest_crop
                    )
                )
                reduce {
                    state.copy(growingCropUiState = growingCropUiState.copy(isInHarvesting = false))
                }
            }
    }

    private fun deleteGrowingCrop(growingCrop: GrowingCrop) = intent {
        val growingCropUiState = state.growingCropUiState
        check(growingCropUiState is GrowingCropUiState.Success)
        reduce { state.copy(growingCropUiState = growingCropUiState.copy(isInDeleting = true)) }
        cropRepository.deleteGrowingCrop(cropId = growingCrop.id)
            .onSuccess {
                reduce {
                    state.copy(
                        growingCropUiState = growingCropUiState.copy(
                            growingCrop = null,
                            isInDeleting = false
                        )
                    )
                }
                postSideEffect(
                    CropSideEffect.Message(
                        defaultRes = R.string.crop_deleted
                    )
                )
                postSideEffect(CropSideEffect.NavigateToPlantScreen)
            }.onFailure {
                postSideEffect(
                    CropSideEffect.Message(
                        content = it.message,
                        defaultRes = R.string.failed_to_delete_crop
                    )
                )
                reduce {
                    state.copy(growingCropUiState = growingCropUiState.copy(isInDeleting = false))
                }
            }
    }
}
