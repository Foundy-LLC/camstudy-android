package io.foundy.crop.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop
import io.foundy.crop.ui.component.GrowingCropDivide
import java.util.Calendar

@Destination
@Composable
fun CropRoute() {
    LazyColumn {
        item {
            GrowingCropDivide(
                growingCrop = GrowingCrop(
                    id = "id",
                    ownerId = "id",
                    type = CropType.CARROT,
                    level = 2,
                    expectedGrade = CropGrade.SILVER,
                    isDead = false,
                    plantedAt = Calendar.getInstance().apply {
                        set(2023, 3, 14, 22, 12)
                    }.time
                )
            )
        }
    }
}

@Composable
fun CropScreen() {
}
