package io.foundy.feature.crop.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.designsystem.util.nonScaledSp
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.HarvestedCrop
import io.foundy.core.ui.crop.getGradeText
import io.foundy.core.ui.crop.getName
import io.foundy.core.ui.crop.imageIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HarvestedCropDialog(
    harvestedCrop: HarvestedCrop,
    onDismissRequest: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val titleSmallFixedSizeTextStyle = CamstudyTheme.typography.titleSmall.copy(
        fontSize = CamstudyTheme.typography.titleSmall.fontSize.nonScaledSp
    )
    val plantedAtString = remember {
        derivedStateOf { dateFormatter.format(harvestedCrop.plantedAt) }
    }
    val harvestedAtString = remember {
        derivedStateOf { dateFormatter.format(harvestedCrop.harvestedAt) }
    }

    Dialog(onDismissRequest = {}) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest
                )
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = CamstudyTheme.colorScheme.cardUi)
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = CamstudyTheme.colorScheme.systemUi01)
                        .padding(10.dp)
                ) {
                    CamstudyIcon(
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                        icon = harvestedCrop.imageIcon,
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                CamstudyText(
                    text = harvestedCrop.type.getName(),
                    style = CamstudyTheme.typography.headlineSmall.copy(
                        color = CamstudyTheme.colorScheme.systemUi08,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Column {
                        val textStyle = titleSmallFixedSizeTextStyle.copy(
                            color = CamstudyTheme.colorScheme.systemUi04
                        )
                        CamstudyText(
                            modifier = Modifier.height(18.dp),
                            text = "심은 날짜",
                            style = textStyle
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CamstudyText(
                            modifier = Modifier.height(18.dp),
                            text = "수확 날짜",
                            style = textStyle
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CamstudyText(
                            modifier = Modifier.height(18.dp),
                            text = "작물 등급",
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
                            text = plantedAtString.value,
                            style = textStyle
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CamstudyText(
                            modifier = Modifier.height(18.dp),
                            text = harvestedAtString.value,
                            style = textStyle
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CamstudyText(
                            modifier = Modifier.height(18.dp),
                            text = harvestedCrop.getGradeText(),
                            style = textStyle
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HarvestedCropDialogPreview() {
    CamstudyTheme {
        HarvestedCropDialog(
            harvestedCrop = HarvestedCrop(
                type = CropType.TOMATO,
                grade = CropGrade.SILVER,
                plantedAt = Date(),
                harvestedAt = Date()
            ),
            onDismissRequest = {}
        )
    }
}
