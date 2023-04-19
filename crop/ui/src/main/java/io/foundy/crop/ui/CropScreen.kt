package io.foundy.crop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.crop.ui.component.GrowingCropDivide
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun CropRoute(
    viewModel: CropViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect {
        when (it) {
            else -> {
                // TODO
            }
        }
    }

    CropScreen(
        growingCropUiState = uiState.growingCropUiState
    )
}

@Composable
fun CropScreen(
    growingCropUiState: GrowingCropUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = CamstudyTheme.colorScheme.systemUi01)
    ) {
        item {
            GrowingCropDivide(growingCropUiState = growingCropUiState)
        }
    }
}
