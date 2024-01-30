package io.foundy.feature.crop.data.model

data class PlantCropRequestBody(
    val userId: String,
    val cropType: CropTypeDto
)
