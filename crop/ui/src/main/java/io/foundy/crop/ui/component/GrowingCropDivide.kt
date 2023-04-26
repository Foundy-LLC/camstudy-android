package io.foundy.crop.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyContainedButton
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.designsystem.util.nonScaledSp
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop
import io.foundy.core.ui.crop.getExpectedGradeText
import io.foundy.core.ui.crop.getFormattedPlantAt
import io.foundy.core.ui.crop.getName
import io.foundy.core.ui.crop.getRemainingTimeText
import io.foundy.core.ui.crop.imageIcon
import io.foundy.crop.ui.GrowingCropUiState
import io.foundy.crop.ui.R
import java.util.Calendar

@Composable
fun GrowingCropDivide(
    growingCropUiState: GrowingCropUiState,
    onPlantClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        DivideTitle(text = stringResource(R.string.my_pot))
        Spacer(modifier = Modifier.height(16.dp))
        DivideContent(
            growingCropUiState = growingCropUiState,
            onPlantClick = onPlantClick
        )
    }
}

@Composable
private fun DivideContent(
    growingCropUiState: GrowingCropUiState,
    onPlantClick: () -> Unit
) {
    val growingCrop = (growingCropUiState as? GrowingCropUiState.Success)?.growingCrop

    Row(verticalAlignment = Alignment.CenterVertically) {
        GrowingCropIcon(growingCrop = growingCrop)
        Spacer(modifier = Modifier.width(16.dp))
        when (growingCropUiState) {
            GrowingCropUiState.Loading -> Box(Modifier.fillMaxWidth()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            is GrowingCropUiState.Success -> {
                if (growingCropUiState.growingCrop != null) {
                    GrowingCropInfo(growingCrop = growingCropUiState.growingCrop)
                } else {
                    EmptyGrowingCropInfo(onPlantClick = onPlantClick)
                }
            }
            is GrowingCropUiState.Failure -> CamstudyText(
                text = growingCropUiState.message ?: stringResource(R.string.failed_to_load_pot),
                style = CamstudyTheme.typography.titleLarge.copy(
                    color = CamstudyTheme.colorScheme.systemUi04,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun GrowingCropIcon(growingCrop: GrowingCrop?) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = CamstudyTheme.colorScheme.systemUi01)
            .padding(10.dp)
    ) {
        val cropIcon = growingCrop?.imageIcon

        if (cropIcon != null) {
            CamstudyIcon(
                modifier = Modifier.fillMaxSize(),
                icon = cropIcon,
                tint = Color.Unspecified,
                contentDescription = null
            )
        } else {
            CamstudyIcon(
                modifier = Modifier.fillMaxSize(),
                icon = CamstudyIcons.EmptyCrop,
                contentDescription = null,
                // TODO: 색상 변경
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun EmptyGrowingCropInfo(onPlantClick: () -> Unit) {
    Column {
        CamstudyText(
            text = stringResource(R.string.pot_is_empty),
            style = CamstudyTheme.typography.titleLarge.copy(
                color = CamstudyTheme.colorScheme.systemUi03,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        CamstudyContainedButton(
            label = stringResource(R.string.plant_crop_into_pot),
            onClick = onPlantClick
        )
    }
}

@Composable
private fun GrowingCropInfo(growingCrop: GrowingCrop) {
    val titleSmallFixedSizeTextStyle = CamstudyTheme.typography.titleSmall.copy(
        fontSize = CamstudyTheme.typography.titleSmall.fontSize.nonScaledSp
    )

    Column {
        Row(
            modifier = Modifier.height(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val cropNameTextStyle = CamstudyTheme.typography.headlineSmall
            val cropLevelTextStyle = CamstudyTheme.typography.titleMedium

            CamstudyText(
                text = growingCrop.getName(),
                style = cropNameTextStyle.copy(
                    fontWeight = FontWeight.Bold,
                    color = CamstudyTheme.colorScheme.systemUi08,
                    fontSize = cropNameTextStyle.fontSize.nonScaledSp
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            CamstudyText(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = CamstudyTheme.colorScheme.systemUi02)
                    .height(26.dp)
                    .padding(horizontal = 9.5.dp, vertical = 2.dp),
                text = stringResource(R.string.crop_level, growingCrop.level),
                style = cropLevelTextStyle.copy(
                    color = CamstudyTheme.colorScheme.systemUi07,
                    fontSize = cropLevelTextStyle.fontSize.nonScaledSp
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        CamstudyText(
            modifier = Modifier.height(18.dp),
            text = growingCrop.getFormattedPlantAt(
                format = stringResource(R.string.growing_crop_plant_at_format)
            ),
            style = titleSmallFixedSizeTextStyle.copy(
                color = CamstudyTheme.colorScheme.systemUi04,
            )
        )
        Spacer(modifier = Modifier.height(11.25.dp))
        CamstudyDivider()
        Spacer(modifier = Modifier.height(11.25.dp))
        Row {
            Column {
                val textStyle = titleSmallFixedSizeTextStyle.copy(
                    color = CamstudyTheme.colorScheme.systemUi04
                )
                CamstudyText(
                    modifier = Modifier.height(18.dp),
                    text = stringResource(R.string.expected_plant_at),
                    style = textStyle
                )
                Spacer(modifier = Modifier.height(8.dp))
                CamstudyText(
                    modifier = Modifier.height(18.dp),
                    text = stringResource(R.string.expected_plant_grade),
                    style = textStyle
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                val textStyle = titleSmallFixedSizeTextStyle.copy(
                    color = CamstudyTheme.colorScheme.systemUi08
                )
                CamstudyText(
                    modifier = Modifier.height(18.dp),
                    text = growingCrop.getRemainingTimeText(),
                    style = textStyle
                )
                Spacer(modifier = Modifier.height(8.dp))
                CamstudyText(
                    modifier = Modifier.height(18.dp),
                    text = growingCrop.getExpectedGradeText(),
                    style = textStyle
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoadingGrowingCropDividePreview() {
    CamstudyTheme {
        GrowingCropDivide(
            growingCropUiState = GrowingCropUiState.Loading,
            onPlantClick = {}
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GrowingCropDividePreview() {
    CamstudyTheme {
        GrowingCropDivide(
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
                )
            ),
            onPlantClick = {}
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmptyGrowingCropDividePreview() {
    CamstudyTheme {
        GrowingCropDivide(
            growingCropUiState = GrowingCropUiState.Success(
                growingCrop = null
            ),
            onPlantClick = {}
        )
    }
}

@Preview
@Composable
fun FailureGrowingCropDividePreview() {
    CamstudyTheme {
        GrowingCropDivide(
            growingCropUiState = GrowingCropUiState.Failure(message = null),
            onPlantClick = {}
        )
    }
}
