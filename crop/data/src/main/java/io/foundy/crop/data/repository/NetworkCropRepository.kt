package io.foundy.crop.data.repository

import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.GrowingCrop
import io.foundy.crop.data.api.CropApi
import io.foundy.crop.data.model.toEntity
import javax.inject.Inject

class NetworkCropRepository @Inject constructor(
    private val api: CropApi
) : CropRepository {

    override suspend fun getGrowingCrop(userId: String): Result<GrowingCrop?> {
        return runCatching {
            val response = api.getGrowingCrop(userId = userId)
            if (response.code() == 404) {
                return@runCatching null
            }
            return@runCatching response.getDataOrThrowMessage().toEntity()
        }
    }
}
