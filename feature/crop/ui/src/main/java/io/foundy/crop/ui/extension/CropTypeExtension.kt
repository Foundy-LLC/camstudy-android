package io.foundy.crop.ui.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.foundy.core.model.CropType
import io.foundy.crop.ui.R

@Composable
fun CropType.getDescription(): String {
    return stringResource(
        id = when (this) {
            CropType.CARROT -> R.string.carrot_description
            CropType.TOMATO -> R.string.momato_description
            CropType.STRAWBERRY -> R.string.strawberry_description
            CropType.PUMPKIN -> R.string.pumpkin_description
            CropType.CABBAGE -> R.string.cabbage_description
        }
    )
}
