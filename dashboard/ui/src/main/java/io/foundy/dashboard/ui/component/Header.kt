package io.foundy.dashboard.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import io.foundy.core.designsystem.component.CamstudyContainedButton
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop
import io.foundy.core.ui.crop.getName
import io.foundy.core.ui.crop.imageIcon
import io.foundy.dashboard.ui.GrowingCropUiState
import io.foundy.dashboard.ui.R
import java.util.Calendar

@Composable
fun Header(
    weeklyStudyMinutes: Int,
    weeklyRanking: Int?,
    growingCropUiState: GrowingCropUiState,
    onCropTileClick: (GrowingCrop?) -> Unit
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
            CamstudyContainedButton(
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
        GrowingCropTile(growingCropUiState = growingCropUiState, onClick = onCropTileClick)
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
    growingCropUiState: GrowingCropUiState,
    onClick: (GrowingCrop?) -> Unit
) {
    when (growingCropUiState) {
        GrowingCropUiState.Loading -> GrowingCropTileSurface {}
        is GrowingCropUiState.Success -> GrowingCropTileContent(
            crop = growingCropUiState.growingCrop,
            onClick = { onClick(growingCropUiState.growingCrop) }
        )
        is GrowingCropUiState.Failure -> GrowingCropTileSurface {
            CamstudyText(
                modifier = Modifier.align(Alignment.Center),
                text = growingCropUiState.message
                    ?: stringResource(R.string.failed_to_load_growing_crop),
                style = CamstudyTheme.typography.titleMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi06
                )
            )
        }
    }
}

@Composable
private fun GrowingCropTileContent(crop: GrowingCrop?, onClick: () -> Unit) {
    val icon = crop?.imageIcon ?: CamstudyIcons.EmptyCrop
    val text = if (crop == null) {
        buildAnnotatedString { append(stringResource(R.string.no_growing_crop)) }
    } else {
        val stateText = stringResource(
            R.string.current_growing_crop,
            crop.getName(),
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
        stringResource(R.string.manage_crop)
    }
    GrowingCropTileSurface(
        onClick = onClick
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

@Composable
private fun GrowingCropTileSurface(
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    var modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 52.dp)
        .clip(RoundedCornerShape(12.dp))
    if (onClick != null) {
        modifier = modifier.clickable(onClick = onClick)
    }
    Surface(
        modifier = modifier,
        color = CamstudyTheme.colorScheme.systemUi01,
    ) {
        Box(content = content)
    }
}

@Preview(widthDp = 360)
@Composable
fun HeaderPreview() {
    CamstudyTheme {
        Header(
            weeklyStudyMinutes = 1132,
            weeklyRanking = 21,
            growingCropUiState = GrowingCropUiState.Success(
                growingCrop = GrowingCrop(
                    id = "id",
                    ownerId = "id",
                    type = CropType.CARROT,
                    level = 2,
                    expectedGrade = CropGrade.SILVER,
                    isDead = false,
                    plantedAt = Calendar.getInstance().apply {
                        set(2023, 3, 1, 2, 12)
                    }.time
                )
            ),
            onCropTileClick = {}
        )
    }
}

@Preview(widthDp = 360)
@Composable
fun GrowingCropErrorHeaderPreview() {
    CamstudyTheme {
        Header(
            weeklyStudyMinutes = 0,
            weeklyRanking = null,
            growingCropUiState = GrowingCropUiState.Failure(message = "서버 내부 에러가 발생했습니다."),
            onCropTileClick = {}
        )
    }
}

@Preview(widthDp = 360)
@Composable
fun GrowingCropLoadingHeaderPreview() {
    CamstudyTheme {
        Header(
            weeklyStudyMinutes = 0,
            weeklyRanking = null,
            growingCropUiState = GrowingCropUiState.Loading,
            onCropTileClick = {}
        )
    }
}
