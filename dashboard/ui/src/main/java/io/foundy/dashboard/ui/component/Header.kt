package io.foundy.dashboard.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.ContainedButton
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop
import io.foundy.core.ui.getName
import io.foundy.dashboard.ui.R
import java.util.Calendar

@Composable
fun Header(
    weeklyStudyMinutes: Int,
    weeklyRanking: Int?,
    growingCrop: GrowingCrop?,
    onCropTileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        DivideTitle(text = stringResource(R.string.weekly_study_minutes))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Time(minutes = weeklyStudyMinutes)
            Spacer(modifier = Modifier.width(12.dp))
            ContainedButton(
                label = stringResource(R.string.see_ranking),
                onClick = { /* TODO */ }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        CamstudyText(
            text = stringResource(R.string.weekly_ranking, weeklyRanking?.toString() ?: "-"),
            style = CamstudyTheme.typography.titleSmall.copy(
                color = CamstudyTheme.colorScheme.systemUi07
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        CamstudyDivider()
        Spacer(modifier = Modifier.height(20.dp))
        GrowingCropTile(crop = growingCrop, onClick = onCropTileClick)
    }
}

@Composable
private fun Time(
    minutes: Int
) {
    val hours = minutes / 60
    val minutesWithoutHour = minutes % 60
    val timeTextStyle = CamstudyTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        color = CamstudyTheme.colorScheme.systemUi08
    )
    val supportTextStyle = CamstudyTheme.typography.titleMedium.copy(
        color = CamstudyTheme.colorScheme.systemUi07
    )

    Row {
        CamstudyText(
            modifier = Modifier.alignByBaseline(),
            text = hours.toString(),
            style = timeTextStyle
        )
        CamstudyText(
            modifier = Modifier.alignByBaseline(),
            text = "시간",
            style = supportTextStyle
        )
        Spacer(modifier = Modifier.width(4.dp))
        CamstudyText(
            modifier = Modifier.alignByBaseline(),
            text = minutesWithoutHour.toString(),
            style = timeTextStyle
        )
        CamstudyText(
            modifier = Modifier.alignByBaseline(),
            text = "분",
            style = supportTextStyle
        )
    }
}

@Composable
fun GrowingCropTile(
    crop: GrowingCrop?,
    onClick: () -> Unit
) {
    val icon = if (crop == null) {
        CamstudyIcons.EmptyCrop
    } else {
        // TODO: 성장중인 작물 아이콘으로 바꾸기
        CamstudyIcons.EmptyCrop
    }
    val text = if (crop == null) {
        buildAnnotatedString { append(stringResource(R.string.no_growing_crop)) }
    } else {
        val stateText = stringResource(
            R.string.current_growing_crop,
            crop.type.getName(),
            crop.level
        )
        buildAnnotatedString {
            append(stringResource(R.string.current_growing_crop_prefix))
            withStyle(style = SpanStyle(color = CamstudyTheme.colorScheme.error)) {
                append(stateText)
            }
        }
    }
    val navigationText = if (crop == null) {
        stringResource(R.string.plant_crop)
    } else {
        stringResource(R.string.manage_plant)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = CamstudyTheme.colorScheme.systemUi01
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CamstudyIcon(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .size(40.dp),
                icon = icon,
                contentDescription = null,
                tint = Color.Unspecified
            )
            CamstudyText(
                modifier = Modifier.weight(1f),
                text = text,
                style = CamstudyTheme.typography.titleSmall.copy(
                    color = CamstudyTheme.colorScheme.systemUi07
                )
            )
            CamstudyText(
                text = navigationText,
                style = CamstudyTheme.typography.labelMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi05
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            CamstudyIcon(
                modifier = Modifier.width(10.dp),
                icon = CamstudyIcons.ArrowForward,
                contentDescription = null,
                tint = CamstudyTheme.colorScheme.systemUi06
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Preview(widthDp = 360)
@Composable
fun HeaderPreview() {
    CamstudyTheme {
        Header(
            weeklyStudyMinutes = 1132,
            weeklyRanking = 21,
            growingCrop = GrowingCrop(
                id = "id",
                ownerId = "id",
                type = CropType.CABBAGE,
                plantedAt = Calendar.getInstance().apply {
                    set(2023, 3, 10, 2, 12)
                }.time
            ),
            onCropTileClick = {}
        )
    }
}

@Preview(widthDp = 360)
@Composable
fun EmptyHeaderPreview() {
    CamstudyTheme {
        Header(
            weeklyStudyMinutes = 0,
            weeklyRanking = null,
            growingCrop = null,
            onCropTileClick = {}
        )
    }
}
