package io.foundy.crop.data.repository

import io.foundy.core.model.GrowingCrop

interface CropRepository {
    suspend fun getGrowingCrop(userId: String): Result<GrowingCrop?>
}
