package io.foundy.core.model

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
    val canHarvest: Boolean = type.maxLevel == level
}
