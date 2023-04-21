package io.foundy.crop.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.HarvestedCrop
import io.foundy.core.ui.gridItems
import io.foundy.core.ui.imageIcon
import io.foundy.crop.ui.R
import java.util.Calendar
import java.util.Date

fun LazyListScope.harvestedCropGridDivide(crops: List<HarvestedCrop>) {
    item {
        DivideTitle(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = CamstudyTheme.colorScheme.systemBackground)
                .padding(16.dp),
            text = stringResource(R.string.my_crops)
        )
        CamstudyDivider()
    }
    if (crops.isEmpty()) {
        emptyHarvestedCrops()
    } else {
        gridItems(
            items = crops,
            nColumns = 4,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
        ) { crop ->
            HarvestedCropItem(crop)
        }
    }
}

@Composable
private fun HarvestedCropItem(
    crop: HarvestedCrop
) {
    Box(
        modifier = Modifier
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .padding(horizontal = 6.dp, vertical = 8.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(color = CamstudyTheme.colorScheme.systemUi01)
            .padding(10.5.dp)
    ) {
        CamstudyIcon(icon = crop.imageIcon, contentDescription = null, tint = Color.Unspecified)
    }
}

private fun LazyListScope.emptyHarvestedCrops() {
    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = CamstudyTheme.colorScheme.systemBackground)
        ) {
            CamstudyText(
                modifier = Modifier
                    .padding(vertical = 60.dp)
                    .align(Alignment.Center),
                text = stringResource(R.string.no_harvested_crops),
                style = CamstudyTheme.typography.titleLarge.copy(
                    color = CamstudyTheme.colorScheme.systemUi03,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Preview
@Composable
private fun HarvestedCropGridDividePreview() {
    val cropTypes = CropType.values()
    val items = Array(20) {
        HarvestedCrop(
            type = cropTypes[it % cropTypes.size],
            grade = CropGrade.GOLD,
            plantedAt = Calendar.getInstance().apply {
                set(2023, 3, 1, 2, it)
            }.time,
            harvestedAt = Date()
        )
    }
    CamstudyTheme {
        LazyColumn {
            harvestedCropGridDivide(
                crops = items.toList()
            )
        }
    }
}

@Preview
@Composable
private fun EmptyHarvestedCropGridDividePreview() {
    CamstudyTheme {
        LazyColumn {
            harvestedCropGridDivide(
                crops = emptyList()
            )
        }
    }
}
