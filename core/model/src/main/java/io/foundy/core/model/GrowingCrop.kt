package io.foundy.core.model

import java.util.Calendar
import java.util.Date

data class GrowingCrop(
    val id: String,
    val ownerId: String,
    val type: CropType,
    val level: Int,
    val expectedGrade: CropGrade,
    val isDead: Boolean,
    val plantedAt: Date
) {
    val remainMinutesToHarvest: Long = run {
        val currentTimeCalendar = Calendar.getInstance()
        val harvestedAtCalendar = Calendar.getInstance().apply {
            time = plantedAt
            add(Calendar.DAY_OF_YEAR, type.requiredDay)
        }
        val diffInMillis = maxOf(
            0,
            harvestedAtCalendar.timeInMillis - currentTimeCalendar.timeInMillis
        )
        return@run diffInMillis / (1000 * 60)
    }

    val canHarvest: Boolean = remainMinutesToHarvest == 0L
}
