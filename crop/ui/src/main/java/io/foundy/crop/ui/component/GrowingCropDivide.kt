package io.foundy.crop.ui.component

import android.content.res.Configuration
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyContainedButton
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyOutlinedButton
import io.foundy.core.designsystem.component.CamstudyText
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
    onPlantClick: () -> Unit,
    onQuestionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .padding(16.dp)
    ) {
        DivideTitle(text = stringResource(R.string.my_pot))
        Spacer(modifier = Modifier.height(16.dp))
        DivideContent(
            growingCropUiState = growingCropUiState,
            onPlantClick = onPlantClick,
            onQuestionClick = onQuestionClick
        )
    }
}

@Composable
private fun DivideContent(
    growingCropUiState: GrowingCropUiState,
    onPlantClick: () -> Unit,
    onQuestionClick: () -> Unit
) {
    val growingCropSuccessUiState = growingCropUiState as? GrowingCropUiState.Success
    val growingCrop = growingCropSuccessUiState?.growingCrop

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GrowingCropIcon(growingCrop = growingCrop)
            Spacer(modifier = Modifier.width(16.dp))
            when (growingCropUiState) {
                GrowingCropUiState.Loading -> Box(Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                is GrowingCropUiState.Success -> {
                    if (growingCropUiState.growingCrop != null) {
                        GrowingCropInfo(
                            growingCrop = growingCropUiState.growingCrop,
                            onQuestionClick = onQuestionClick
                        )
                    } else {
                        EmptyGrowingCropInfo(onPlantClick = onPlantClick)
                    }
                }
                is GrowingCropUiState.Failure -> CamstudyText(
                    text = growingCropUiState.message
                        ?: stringResource(R.string.failed_to_load_pot),
                    style = CamstudyTheme.typography.titleLarge.copy(
                        color = CamstudyTheme.colorScheme.systemUi04,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
        if (growingCrop != null) {
            Spacer(modifier = Modifier.height(20.dp))
            if (growingCrop.isDead) {
                var showReplantCropDialog by remember { mutableStateOf(false) }

                if (showReplantCropDialog) {
                    CamstudyDialog(
                        content = stringResource(R.string.replant_dialog_content),
                        onDismissRequest = { showReplantCropDialog = false },
                        onCancel = { showReplantCropDialog = false },
                        confirmText = stringResource(id = R.string.replant),
                        onConfirm = {
                            showReplantCropDialog = false
                            growingCropSuccessUiState.onReplantClick(growingCrop)
                        }
                    )
                }

                ReplantButton(
                    onClick = { showReplantCropDialog = true }
                )
            } else {
                HarvestButton(
                    enabled = growingCrop.canHarvest && !growingCropSuccessUiState.isInHarvesting,
                    onClick = { growingCropSuccessUiState.onHarvestClick(growingCrop) }
                )
            }
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
                tint = if (growingCrop.isDead) {
                    CamstudyTheme.colorScheme.systemUi04
                } else {
                    Color.Unspecified
                },
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
private fun GrowingCropInfo(growingCrop: GrowingCrop, onQuestionClick: () -> Unit) {
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
                text = if (growingCrop.isDead) {
                    stringResource(R.string.dead)
                } else {
                    stringResource(R.string.crop_level, growingCrop.level)
                },
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
                    text = if (growingCrop.isDead) {
                        stringResource(R.string.empty_content)
                    } else {
                        growingCrop.getRemainingTimeText()
                    },
                    style = textStyle
                )
                Row(
                    modifier = Modifier
                        .clickable(onClick = onQuestionClick)
                        .padding(top = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CamstudyText(
                        modifier = Modifier.height(18.dp),
                        text = if (growingCrop.isDead) {
                            stringResource(R.string.empty_content)
                        } else {
                            growingCrop.getExpectedGradeText()
                        },
                        style = textStyle
                    )
                    CamstudyIcon(
                        modifier = Modifier.size(18.dp),
                        icon = CamstudyIcons.Question,
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

@Composable
private fun ReplantButton(onClick: () -> Unit) {
    CamstudyOutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        label = stringResource(R.string.replant),
        onClick = onClick
    )
}

@Composable
private fun HarvestButton(enabled: Boolean, onClick: () -> Unit) {
    CamstudyContainedButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        label = stringResource(R.string.harvest),
        onClick = onClick,
        enabled = enabled
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoadingGrowingCropDividePreview() {
    CamstudyTheme {
        GrowingCropDivide(
            growingCropUiState = GrowingCropUiState.Loading,
            onPlantClick = {},
            onQuestionClick = {}
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
                ),
                onHarvestClick = {},
                onReplantClick = {},
            ),
            onPlantClick = {},
            onQuestionClick = {}
        )
    }
}

@Preview
@Composable
fun GrowingCropDivideCanHarvestPreview() {
    CamstudyTheme {
        GrowingCropDivide(
            growingCropUiState = GrowingCropUiState.Success(
                growingCrop = GrowingCrop(
                    id = "id",
                    ownerId = "id",
                    type = CropType.CARROT,
                    level = CropType.CARROT.maxLevel,
                    expectedGrade = CropGrade.SILVER,
                    isDead = false,
                    plantedAt = Calendar.getInstance().apply {
                        set(2023, 3, 14, 21, 59)
                    }.time
                ),
                onHarvestClick = {},
                onReplantClick = {},
            ),
            onPlantClick = {},
            onQuestionClick = {}
        )
    }
}

@Preview
@Composable
fun GrowingCropDivideDeadPreview() {
    CamstudyTheme {
        GrowingCropDivide(
            growingCropUiState = GrowingCropUiState.Success(
                growingCrop = GrowingCrop(
                    id = "id",
                    ownerId = "id",
                    type = CropType.CARROT,
                    level = 3,
                    expectedGrade = CropGrade.SILVER,
                    isDead = true,
                    plantedAt = Calendar.getInstance().apply {
                        set(2023, 3, 14, 21, 59)
                    }.time
                ),
                onHarvestClick = {},
                onReplantClick = {},
            ),
            onPlantClick = {},
            onQuestionClick = {}
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
                growingCrop = null,
                onHarvestClick = {},
                onReplantClick = {},
            ),
            onPlantClick = {},
            onQuestionClick = {}
        )
    }
}

@Preview
@Composable
fun FailureGrowingCropDividePreview() {
    CamstudyTheme {
        GrowingCropDivide(
            growingCropUiState = GrowingCropUiState.Failure(message = null),
            onPlantClick = {},
            onQuestionClick = {}
        )
    }
}
