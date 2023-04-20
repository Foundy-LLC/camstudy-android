package io.foundy.crop.data.model

data class PlantCropRequestBody(
    val userId: String,
    val cropType: CropTypeDto
)
