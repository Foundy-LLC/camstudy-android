package io.foundy.feature.crop.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.model.CropGrade

enum class CropGradeDto {

    @SerializedName("not_fresh")
    NOT_FRESH,

    @SerializedName("fresh")
    FRESH,

    @SerializedName("silver")
    SILVER,

    @SerializedName("gold")
    GOLD,

    @SerializedName("diamond")
    DIAMOND
}

fun CropGradeDto.toEntity() = when (this) {
    CropGradeDto.NOT_FRESH -> CropGrade.NOT_FRESH
    CropGradeDto.FRESH -> CropGrade.FRESH
    CropGradeDto.SILVER -> CropGrade.SILVER
    CropGradeDto.GOLD -> CropGrade.GOLD
    CropGradeDto.DIAMOND -> CropGrade.DIAMOND
}
