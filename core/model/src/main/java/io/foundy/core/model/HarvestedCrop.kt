package io.foundy.core.model

import java.util.Date

data class HarvestedCrop(
    val type: CropType,
    val grade: CropGrade,
    val plantedAt: Date,
    val harvestedAt: Date
)
