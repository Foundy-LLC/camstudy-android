package io.foundy.crop.data.repository

import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop
import kotlinx.coroutines.flow.Flow

interface CropRepository {
    val currentUserGrowingCropFlow: Flow<GrowingCrop?>
    suspend fun getGrowingCrop(userId: String): Result<GrowingCrop?>
    suspend fun getHarvestedCrops(userId: String): Result<List<HarvestedCrop>>
    suspend fun plantCrop(cropType: CropType): Result<Unit>
    suspend fun harvestCrop(cropId: String): Result<Unit>
}
