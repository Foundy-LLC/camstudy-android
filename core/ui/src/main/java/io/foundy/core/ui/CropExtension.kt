package io.foundy.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.foundy.core.common.util.currentLocale
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.asCamstudyIcon
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

private fun throwInvalidLevelException(level: Int): Nothing {
    error("There is invalid level of the crop. Level: $level")
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
                else -> throwInvalidLevelException(this.level)
            }
            CropType.TOMATO -> when (currentLevel) {
                1 -> R.drawable.plant_tomato_1
                2 -> R.drawable.plant_tomato_2
                3 -> R.drawable.plant_tomato_3
                4 -> R.drawable.plant_tomato_4
                CropType.TOMATO.maxLevel -> R.drawable.plant_tomato_5
                else -> throwInvalidLevelException(this.level)
            }
            CropType.CARROT -> when (currentLevel) {
                1 -> R.drawable.plant_carrot_1
                2 -> R.drawable.plant_carrot_2
                CropType.CARROT.maxLevel -> R.drawable.plant_carrot_3
                else -> throwInvalidLevelException(this.level)
            }
            CropType.PUMPKIN -> when (currentLevel) {
                1 -> R.drawable.plant_pumpkin_1
                2 -> R.drawable.plant_pumpkin_2
                3 -> R.drawable.plant_pumpkin_3
                4 -> R.drawable.plant_pumpkin_4
                CropType.PUMPKIN.maxLevel -> R.drawable.plant_pumpkin_5
                else -> throwInvalidLevelException(this.level)
            }
            CropType.CABBAGE -> when (currentLevel) {
                1 -> R.drawable.plant_cabbage_1
                2 -> R.drawable.plant_cabbage_2
                3 -> R.drawable.plant_cabbage_3
                4 -> R.drawable.plant_cabbage_4
                CropType.CABBAGE.maxLevel -> R.drawable.plant_cabbage_5
                else -> throwInvalidLevelException(this.level)
            }
        }.asCamstudyIcon()
    }

@Composable
fun GrowingCrop.getName(): String {
    return this.type.getName()
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

@Composable
fun GrowingCrop.getExpectedGradeText(): String {
    return stringResource(
        id = when (this.expectedGrade) {
            CropGrade.NOT_FRESH -> R.string.not_fresh
            CropGrade.FRESH -> R.string.fresh
            CropGrade.SILVER -> R.string.silver
            CropGrade.GOLD -> R.string.gold
            CropGrade.DIAMOND -> R.string.diamond
        }
    )
}

@Composable
fun GrowingCrop.getFormattedPlantAt(format: String): String {
    return SimpleDateFormat(
        format,
        LocalContext.current.currentLocale
    ).format(plantedAt)
}

private fun GrowingCrop.getRemainingTimeInMinutes(): Long {
    val currentTimeCalendar = Calendar.getInstance()
    val harvestedAtCalendar = Calendar.getInstance().apply {
        time = this@getRemainingTimeInMinutes.plantedAt
        add(Calendar.DAY_OF_YEAR, this@getRemainingTimeInMinutes.type.requiredDay)
    }
    val diffInMillis = maxOf(
        0,
        harvestedAtCalendar.timeInMillis - currentTimeCalendar.timeInMillis
    )
    return diffInMillis / (1000 * 60)
}

@Composable
fun GrowingCrop.getRemainingTimeText(): String {
    val remainingMinutes = this.getRemainingTimeInMinutes()
    val days = TimeUnit.MINUTES.toDays(remainingMinutes)
    val hours = TimeUnit.MINUTES.toHours(remainingMinutes) % 24
    val minutes = TimeUnit.MINUTES.toMinutes(remainingMinutes) % 60
    val dayStringResource = stringResource(id = R.string.day)
    val hourStringResource = stringResource(id = R.string.hour)
    val minuteStringResource = stringResource(id = R.string.minute)

    if (remainingMinutes == 0L) {
        return stringResource(R.string.can_harvest)
    }

    var result = ""
    if (days > 0) {
        result += "${days}${dayStringResource} "
    }
    if (hours > 0) {
        result += "${hours}${hourStringResource} "
    }
    if (minutes > 0) {
        result += "${minutes}${minuteStringResource} "
    }

    return stringResource(id = R.string.remaining_time, result.trim())
}
