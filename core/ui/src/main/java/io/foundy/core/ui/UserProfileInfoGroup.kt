package io.foundy.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop
import io.foundy.core.ui.crop.getName
import io.foundy.core.ui.crop.imageIcon
import io.foundy.core.ui.util.secToHourMinuteFormat

@Composable
fun UserProfileInfoGroup(
    modifier: Modifier = Modifier,
    weeklyRanking: Int,
    harvestedCrops: List<HarvestedCrop>,
    growingCrop: GrowingCrop?,
    weeklyRankingOverall: Int,
    weeklyStudyTimeSec: Int,
    consecutiveStudyDays: Int
) {
    Column(
        modifier = modifier
    ) {
        InfoTile(
            leadingIcon = CamstudyIcons.Ranking,
            title = stringResource(R.string.user_dialog_weekly_ranking_title),
            content = {
                CamstudyText(
                    text = stringResource(
                        R.string.user_dialog_ranking_content,
                        weeklyRanking
                    ),
                    style = CamstudyTheme.typography.titleMedium.copy(
                        color = CamstudyTheme.colorScheme.systemUi08,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            trailingInfo = stringResource(
                R.string.user_dialog_weekly_ranking_overall_info,
                weeklyRankingOverall
            )
        )
        InfoTile(
            leadingIcon = CamstudyIcons.AccessTimeFilled,
            title = stringResource(R.string.user_dialog_weekly_study_time_title),
            content = {
                CamstudyText(
                    text = if (weeklyStudyTimeSec == 0) {
                        stringResource(R.string.none)
                    } else {
                        weeklyStudyTimeSec.secToHourMinuteFormat()
                    },
                    style = CamstudyTheme.typography.titleSmall.copy(
                        color = CamstudyTheme.colorScheme.systemUi08,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
            trailingInfo = stringResource(
                R.string.user_dialog_consecutive_study_days,
                consecutiveStudyDays
            )
        )
        InfoTile(
            leadingIcon = CamstudyIcons.Crop,
            title = stringResource(R.string.user_dialog_plant_pot_title),
            content = {
                if (growingCrop != null) {
                    CamstudyIcon(
                        icon = growingCrop.imageIcon,
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            },
            trailingInfo = if (growingCrop != null) {
                stringResource(
                    R.string.user_dialog_growing_crop_info,
                    growingCrop.getName(),
                    growingCrop.level
                )
            } else {
                stringResource(R.string.user_dialog_empty)
            }
        )
        InfoTile(
            leadingIcon = CamstudyIcons.Leaf,
            title = stringResource(R.string.user_dialog_harvested_crops_title),
            content = {
                var visibleCropCount by remember { mutableStateOf(0) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HarvestedCropRow(
                        crops = harvestedCrops,
                        onPlacementComplete = { visibleCropCount = it }
                    )
                    if (visibleCropCount != harvestedCrops.size) {
                        CamstudyText(
                            text = stringResource(R.string.ellipsis),
                            style = CamstudyTheme.typography.labelMedium.copy(
                                color = CamstudyTheme.colorScheme.systemUi05,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            },
            trailingInfo = if (harvestedCrops.isEmpty()) {
                stringResource(R.string.user_dialog_empty_harvested_crops)
            } else {
                stringResource(
                    R.string.user_dialog_harvested_crops_content,
                    harvestedCrops.size
                )
            }
        )
    }
}

@Composable
private fun InfoTile(
    leadingIcon: CamstudyIcon,
    title: String,
    content: @Composable () -> Unit,
    trailingInfo: String
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .height(24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CamstudyIcon(
                icon = leadingIcon,
                contentDescription = null,
                tint = CamstudyTheme.colorScheme.systemUi09
            )
            Spacer(modifier = Modifier.width(4.dp))
            CamstudyText(
                text = title,
                style = CamstudyTheme.typography.titleSmall.copy(
                    color = CamstudyTheme.colorScheme.systemUi08,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
        ) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                content()
            }
        }
        CamstudyText(
            text = trailingInfo,
            style = CamstudyTheme.typography.labelMedium.copy(
                color = CamstudyTheme.colorScheme.systemUi06,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

private data class CropRowItem(val placeable: Placeable, val xPosition: Int)

@Composable
private fun HarvestedCropRow(
    crops: List<HarvestedCrop>,
    onPlacementComplete: (visibleCropCount: Int) -> Unit,
) {
    Layout(
        content = {
            for (crop in crops) {
                CamstudyIcon(
                    modifier = Modifier.padding(end = 8.dp),
                    icon = crop.imageIcon,
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val items = mutableListOf<CropRowItem>()
        var xPosition = 0

        for (placeable in placeables) {
            if (xPosition + placeable.width > constraints.maxWidth) {
                break
            }
            items.add(CropRowItem(placeable, xPosition))
            xPosition += placeable.width
        }

        layout(
            width = items.lastOrNull()?.let { it.xPosition + it.placeable.width } ?: 0,
            height = items.maxOfOrNull { it.placeable.height } ?: 0
        ) {
            for (item in items) {
                item.placeable.place(item.xPosition, 0)
            }
            onPlacementComplete(items.count())
        }
    }
}
