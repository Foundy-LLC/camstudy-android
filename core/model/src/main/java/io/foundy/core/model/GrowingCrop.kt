package io.foundy.core.model

import java.util.Date

data class GrowingCrop(
    val id: String,
    val ownerId: String,
    val type: CropType,
    val plantedAt: Date
) {
    private fun dayToMilli(day: Int): Long {
        return day * 24L * 60L * 60 * 1000
    }

    val level: Int = run {
        val elapsedMilli = Date().time - plantedAt.time
        val currentProgressPercent = (100f * elapsedMilli / dayToMilli(type.requiredDay))
        if (currentProgressPercent >= 100) {
            return@run type.maxLevel
        }
        val oneLevelPercent = 100f / type.maxLevel
        return@run (currentProgressPercent / oneLevelPercent).toInt() + 1
    }
}
