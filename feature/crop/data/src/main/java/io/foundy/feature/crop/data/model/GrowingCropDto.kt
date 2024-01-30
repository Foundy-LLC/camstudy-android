package io.foundy.feature.crop.data.model

import io.foundy.core.model.GrowingCrop
import java.util.Date

data class GrowingCropDto(
    val id: String,
    val ownerId: String,
    val type: CropTypeDto,
    val level: Int,
    val expectedGrade: CropGradeDto,
    val isDead: Boolean,
    val plantedAt: Date
)

fun GrowingCropDto.toEntity() = GrowingCrop(
    id = id,
    ownerId = ownerId,
    type = type.toEntity(),
    level = level,
    expectedGrade = expectedGrade.toEntity(),
    isDead = isDead,
    plantedAt = plantedAt
)
