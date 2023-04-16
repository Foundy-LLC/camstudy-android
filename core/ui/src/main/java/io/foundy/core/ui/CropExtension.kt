package io.foundy.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.asCamstudyIcon
import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop

private fun throwInvalidLevelException(): Nothing {
    throw IllegalStateException("There is invalid level of the crop.")
}

val GrowingCrop.imageIcon
    get(): CamstudyIcon {
        val currentLevel = this.level
        return when (this.type) {
            CropType.STRAWBERRY -> when (currentLevel) {
                1 -> R.drawable.plant_strawberry_1
                2 -> R.drawable.plant_strawberry_2
                3 -> R.drawable.plant_strawberry_3
                4 -> R.drawable.plant_strawberry_4
                CropType.STRAWBERRY.maxLevel -> R.drawable.plant_strawberry_5
                else -> throwInvalidLevelException()
            }
            CropType.TOMATO -> when (currentLevel) {
                1 -> R.drawable.plant_tomato_1
                2 -> R.drawable.plant_tomato_2
                3 -> R.drawable.plant_tomato_3
                4 -> R.drawable.plant_tomato_4
                CropType.TOMATO.maxLevel -> R.drawable.plant_tomato_5
                else -> throwInvalidLevelException()
            }
            CropType.CARROT -> when (currentLevel) {
                1 -> R.drawable.plant_carrot_1
                2 -> R.drawable.plant_carrot_2
                CropType.CARROT.maxLevel -> R.drawable.plant_carrot_3
                else -> throwInvalidLevelException()
            }
            CropType.PUMPKIN -> when (currentLevel) {
                1 -> R.drawable.plant_pumpkin_1
                2 -> R.drawable.plant_pumpkin_2
                3 -> R.drawable.plant_pumpkin_3
                4 -> R.drawable.plant_pumpkin_4
                CropType.PUMPKIN.maxLevel -> R.drawable.plant_pumpkin_5
                else -> throwInvalidLevelException()
            }
            CropType.CABBAGE -> when (currentLevel) {
                1 -> R.drawable.plant_cabbage_1
                2 -> R.drawable.plant_cabbage_2
                3 -> R.drawable.plant_cabbage_3
                4 -> R.drawable.plant_cabbage_4
                CropType.CABBAGE.maxLevel -> R.drawable.plant_cabbage_5
                else -> throwInvalidLevelException()
            }
        }.asCamstudyIcon()
    }

@Composable
fun CropType.getName(): String {
    return stringResource(
        id = when (this) {
            CropType.STRAWBERRY -> R.string.strawberry
            CropType.TOMATO -> R.string.tomato
            CropType.CARROT -> R.string.carrot
            CropType.PUMPKIN -> R.string.pumpkin
            CropType.CABBAGE -> R.string.cabbage
        }
    )
}
