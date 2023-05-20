package io.foundy.crop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop
import io.foundy.core.ui.pullrefresh.RefreshableContent
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
    var showCropQuestionDialog by remember { mutableStateOf(false) }

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

    if (showCropQuestionDialog) {
        CamstudyDialog(
            title = stringResource(R.string.crop_question_title),
            content = stringResource(R.string.crop_question_content),
            onDismissRequest = { showCropQuestionDialog = false },
            onConfirm = { showCropQuestionDialog = false }
        )
    }

    CropScreen(
        uiState = uiState,
        onPlantClick = navigateToPlantScreen,
        onQuestionClick = { showCropQuestionDialog = true }
    )
}

@Composable
fun CropScreen(
    uiState: CropUiState,
    onPlantClick: () -> Unit,
    onQuestionClick: () -> Unit,
) {
    RefreshableContent(
        modifier = Modifier
            .fillMaxSize()
            .background(color = CamstudyTheme.colorScheme.systemUi01),
        refreshing = uiState.isRefreshing,
        onRefresh = uiState.onRefreshing
    ) {
        LazyColumn {
            item {
                GrowingCropDivide(
                    growingCropUiState = uiState.growingCropUiState,
                    onPlantClick = onPlantClick,
                    onQuestionClick = onQuestionClick
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            harvestedCropGridDivide(harvestedCropsUiState = uiState.harvestedCropsUiState)
        }
    }
}

@Preview
@Composable
private fun CropScreenPreview() {
    CamstudyTheme {
        CropScreen(
            uiState = CropUiState(
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
                onRefreshing = {},
                fetchGrowingCrop = {}
            ),
            onPlantClick = {},
            onQuestionClick = {}
        )
    }
}
