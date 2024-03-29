package io.foundy.feature.crop.ui.plant

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import io.foundy.core.designsystem.component.BottomContainedButton
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.CropType
import io.foundy.core.ui.crop.freshIcon
import io.foundy.core.ui.crop.getName
import io.foundy.feature.crop.ui.R
import io.foundy.feature.crop.ui.component.DivideTitle
import io.foundy.feature.crop.ui.extension.getDescription
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
@Destination
fun PlantCropRoute(
    resultNavigator: ResultBackNavigator<Boolean>,
    viewModel: PlantCropViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    viewModel.collectSideEffect {
        when (it) {
            is PlantCropSideEffect.FailedToPlant -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.message ?: context.getString(R.string.failed_to_plant_crop)
                )
            }
            PlantCropSideEffect.SuccessToPlant -> resultNavigator.navigateBack(result = true)
        }
    }

    PlantCropScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = resultNavigator::navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantCropScreen(
    uiState: PlantCropUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit
) {
    val selectedCropName = uiState.selectedCropType?.getName()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CamstudyTopAppBar(
                title = { CamstudyText(text = stringResource(R.string.plant_crop)) },
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = CamstudyTheme.colorScheme.systemUi01)
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    SelectedCropDivide(selectedCropType = uiState.selectedCropType)
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        DivideTitle(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = CamstudyTheme.colorScheme.systemBackground)
                                .padding(16.dp),
                            text = stringResource(R.string.crop_list)
                        )
                        CamstudyDivider(modifier = Modifier.align(Alignment.BottomCenter))
                    }
                }
                items(items = CropType.values()) { cropType ->
                    Box {
                        SelectableCropTile(
                            cropType = cropType,
                            selected = uiState.selectedCropType == cropType,
                            onClick = { uiState.onSelected(cropType) }
                        )
                        CamstudyDivider(modifier = Modifier.align(Alignment.BottomCenter))
                    }
                }
            }
            BottomContainedButton(
                enabled = uiState.canPlant,
                label = if (selectedCropName != null) {
                    stringResource(
                        if (uiState.isPlanting) {
                            R.string.planting_selected_crop
                        } else {
                            R.string.plant_selected_crop
                        },
                        selectedCropName
                    )
                } else {
                    stringResource(id = R.string.plant_crop)
                },
                onClick = uiState.onPlantClick
            )
        }
    }
}

@Composable
private fun SelectedCropDivide(selectedCropType: CropType?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .padding(top = 20.dp, bottom = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        DivideTitle(text = stringResource(id = R.string.selected_crop))
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = CamstudyTheme.colorScheme.systemUi01)
                    .padding(10.dp)
            ) {
                val icon = selectedCropType?.freshIcon ?: CamstudyIcons.EmptyCrop
                CamstudyIcon(
                    modifier = Modifier.fillMaxSize(),
                    icon = icon,
                    tint = Color.Unspecified,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (selectedCropType != null) {
                SelectedCropDivideInfo(cropType = selectedCropType)
            } else {
                CamstudyText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(R.string.select_crop_in_list),
                    style = CamstudyTheme.typography.titleLarge.copy(
                        color = CamstudyTheme.colorScheme.systemUi04,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun SelectedCropDivideInfo(cropType: CropType) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CamstudyText(
                modifier = Modifier.animateContentSize(),
                text = cropType.getName(),
                style = CamstudyTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = CamstudyTheme.colorScheme.systemUi08
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = CamstudyTheme.colorScheme.systemUi02)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                CamstudyText(
                    text = stringResource(R.string.selected_crop_max_level, cropType.maxLevel),
                    style = CamstudyTheme.typography.titleMedium.copy(
                        color = CamstudyTheme.colorScheme.systemUi07
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            CamstudyText(
                text = stringResource(R.string.required_day_to_harvest),
                style = CamstudyTheme.typography.titleMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi04
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            CamstudyText(
                text = stringResource(R.string.required_day, cropType.requiredDay),
                style = CamstudyTheme.typography.titleMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi08,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Composable
private fun SelectableCropTile(
    cropType: CropType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = if (selected) {
                    CamstudyTheme.colorScheme.systemUi01
                } else {
                    CamstudyTheme.colorScheme.systemBackground
                }
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = if (selected) {
                        CamstudyTheme.colorScheme.systemBackground
                    } else {
                        CamstudyTheme.colorScheme.systemUi01
                    }
                )
                .padding(10.dp)
        ) {
            CamstudyIcon(
                icon = cropType.freshIcon,
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            CamstudyText(
                text = cropType.getName(),
                style = CamstudyTheme.typography.titleMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi09,
                    fontWeight = FontWeight.SemiBold
                )
            )
            CamstudyText(
                text = cropType.getDescription(),
                style = CamstudyTheme.typography.titleSmall.copy(
                    color = CamstudyTheme.colorScheme.systemUi05,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Preview
@Composable
private fun EmptySelectedCropDividePreview() {
    CamstudyTheme {
        SelectedCropDivide(selectedCropType = null)
    }
}

@Preview
@Composable
private fun SelectedCropDividePreview() {
    CamstudyTheme {
        SelectedCropDivide(selectedCropType = CropType.CABBAGE)
    }
}

@Preview
@Composable
private fun SelectableCropTilePreview() {
    CamstudyTheme {
        SelectableCropTile(
            cropType = CropType.PUMPKIN,
            selected = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun PlantCropScreenPreview() {
    CamstudyTheme {
        PlantCropScreen(
            uiState = PlantCropUiState(
                selectedCropType = CropType.STRAWBERRY,
                onPlantClick = {},
                onSelected = {}
            ),
            onBackClick = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview
@Composable
private fun PlantCropScreenInPlantingPreview() {
    CamstudyTheme {
        PlantCropScreen(
            uiState = PlantCropUiState(
                selectedCropType = CropType.STRAWBERRY,
                isPlanting = true,
                onPlantClick = {},
                onSelected = {}
            ),
            onBackClick = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
