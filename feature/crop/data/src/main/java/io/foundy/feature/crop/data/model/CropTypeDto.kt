package io.foundy.feature.crop.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.model.CropType

enum class CropTypeDto {

    @SerializedName("carrot")
    CARROT,

    @SerializedName("tomato")
    TOMATO,

    @SerializedName("strawberry")
    STRAWBERRY,

    @SerializedName("pumpkin")
    PUMPKIN,

    @SerializedName("cabbage")
    CABBAGE
}

fun CropTypeDto.toEntity(): CropType {
    return when (this) {
        CropTypeDto.CARROT -> CropType.CARROT
        CropTypeDto.TOMATO -> CropType.TOMATO
        CropTypeDto.STRAWBERRY -> CropType.STRAWBERRY
        CropTypeDto.PUMPKIN -> CropType.PUMPKIN
        CropTypeDto.CABBAGE -> CropType.CABBAGE
    }
}

fun CropType.toDto(): CropTypeDto {
    return when (this) {
        CropType.CARROT -> CropTypeDto.CARROT
        CropType.TOMATO -> CropTypeDto.TOMATO
        CropType.STRAWBERRY -> CropTypeDto.STRAWBERRY
        CropType.PUMPKIN -> CropTypeDto.PUMPKIN
        CropType.CABBAGE -> CropTypeDto.CABBAGE
    }
}
