package io.foundy.crop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop
import io.foundy.crop.ui.component.GrowingCropDivide
import io.foundy.crop.ui.component.harvestedCropGridDivide
import io.foundy.crop.ui.destinations.PlantCropRouteDestination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.util.Calendar
import java.util.Date

@Destination
@Composable
fun CropRoute(
    navigator: DestinationsNavigator,
    plantResultRecipient: OpenResultRecipient<Boolean>,
    showSnackbar: (String) -> Unit,
    viewModel: CropViewModel = hiltViewModel(),
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current
    val navigateToPlantScreen = {
        navigator.navigate(PlantCropRouteDestination)
    }

    plantResultRecipient.onNavResult {
        when (it) {
            is NavResult.Value -> {
                uiState.fetchGrowingCrop()
                showSnackbar(context.getString(R.string.success_to_plant_crop))
            }
            NavResult.Canceled -> {}
        }
    }

    viewModel.collectSideEffect {
        when (it) {
            is CropSideEffect.Message -> showSnackbar(
                it.content ?: context.getString(it.defaultRes)
            )
            CropSideEffect.NavigateToPlantScreen -> navigateToPlantScreen()
        }
    }

    CropScreen(
        growingCropUiState = uiState.growingCropUiState,
        harvestedCropsUiState = uiState.harvestedCropsUiState,
        onPlantClick = navigateToPlantScreen,
    )
}

@Composable
fun CropScreen(
    growingCropUiState: GrowingCropUiState,
    harvestedCropsUiState: HarvestedCropsUiState,
    onPlantClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = CamstudyTheme.colorScheme.systemUi01)
    ) {
        item {
            GrowingCropDivide(
                growingCropUiState = growingCropUiState,
                onPlantClick = onPlantClick,
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        harvestedCropGridDivide(harvestedCropsUiState = harvestedCropsUiState)
    }
}

@Preview
@Composable
private fun CropScreenPreview() {
    CamstudyTheme {
        CropScreen(
            growingCropUiState = GrowingCropUiState.Success(
                growingCrop = GrowingCrop(
                    id = "id",
                    ownerId = "id",
                    type = CropType.CARROT,
                    level = 2,
                    expectedGrade = CropGrade.SILVER,
                    isDead = false,
                    plantedAt = Calendar.getInstance().apply {
                        set(2023, 3, 14, 21, 59)
                    }.time
                ),
                onHarvestClick = {},
                onReplantClick = {}
            ),
            harvestedCropsUiState = HarvestedCropsUiState.Success(
                harvestedCrops = listOf(
                    HarvestedCrop(
                        type = CropType.CARROT,
                        grade = CropGrade.GOLD,
                        plantedAt = Calendar.getInstance().apply {
                            set(2023, 3, 1, 2, 2)
                        }.time,
                        harvestedAt = Date()
                    )
                )
            ),
            onPlantClick = {},
        )
    }
}
