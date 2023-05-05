package io.foundy.crop.data.repository

import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.CropType
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop
import io.foundy.crop.data.api.CropApi
import io.foundy.crop.data.model.PlantCropRequestBody
import io.foundy.crop.data.model.toDto
import io.foundy.crop.data.model.toEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class NetworkCropRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val cropApi: CropApi
) : CropRepository {

    override val currentUserGrowingCropFlow: MutableSharedFlow<GrowingCrop?> = MutableSharedFlow(1)

    override suspend fun getGrowingCrop(userId: String): Result<GrowingCrop?> {
        val isCurrentUser = userId == authRepository.currentUserIdStream.firstOrNull()
        return runCatching {
            val response = cropApi.getGrowingCrop(userId = userId)
            if (response.code() == 404) {
                if (isCurrentUser) {
                    currentUserGrowingCropFlow.emit(null)
                }
                return@runCatching null
            }
            val growingCrop = response.getDataOrThrowMessage().toEntity()
            if (isCurrentUser) {
                currentUserGrowingCropFlow.emit(growingCrop)
            }
            return@runCatching growingCrop
        }
    }

    override suspend fun getHarvestedCrops(userId: String): Result<List<HarvestedCrop>> {
        return runCatching {
            val response = cropApi.getHarvestedCrops(userId = userId)
            response.getDataOrThrowMessage().map { it.toEntity() }
        }
    }

    override suspend fun plantCrop(cropType: CropType): Result<Unit> {
        val currentUserId = authRepository.currentUserIdStream.firstOrNull()
        check(currentUserId != null) {
            "현재 회원의 아이디가 없습니다. 로그인 하지 않고 작물을 심으려 했습니다."
        }
        return runCatching {
            val response = cropApi.plantCrop(
                body = PlantCropRequestBody(userId = currentUserId, cropType = cropType.toDto())
            )
            return@runCatching response.getDataOrThrowMessage()
        }
    }

    override suspend fun harvestCrop(cropId: String): Result<Unit> {
        return runCatching {
            val currentUserId = authRepository.currentUserIdStream.firstOrNull()
            check(currentUserId != null) {
                "현재 회원의 아이디가 없습니다. 로그인 하지 않고 작물을 수확하려 했습니다."
            }
            val response = cropApi.harvestCrop(userId = currentUserId, cropId = cropId)
            response.getDataOrThrowMessage()
        }
    }

    override suspend fun deleteGrowingCrop(cropId: String): Result<Unit> {
        return runCatching {
            val currentUserId = authRepository.currentUserIdStream.firstOrNull()
            check(currentUserId != null) {
                "현재 회원의 아이디가 없습니다. 로그인 하지 않고 작물을 제거하려 했습니다."
            }
            val response = cropApi.deleteCrop(userId = currentUserId, cropId = cropId)
            response.getDataOrThrowMessage()
        }
    }
}
