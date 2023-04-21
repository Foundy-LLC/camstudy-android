package io.foundy.crop.data.model

import io.foundy.core.model.HarvestedCrop
import java.util.Date

data class HarvestedCropDto(
    val type: CropTypeDto,
    val grade: CropGradeDto,
    val plantedAt: Date,
    val harvestedAt: Date
)

fun HarvestedCropDto.toEntity() = HarvestedCrop(
    type = type.toEntity(),
    grade = grade.toEntity(),
    plantedAt = plantedAt,
    harvestedAt = harvestedAt
)
