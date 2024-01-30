package io.foundy.feature.crop.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.feature.crop.data.model.GrowingCropDto
import io.foundy.feature.crop.data.model.HarvestedCropDto
import io.foundy.feature.crop.data.model.PlantCropRequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CropApi {

    @GET("crops/{userId}/growing")
    suspend fun getGrowingCrop(
        @Path("userId") userId: String
    ): CamstudyResponse<GrowingCropDto>

    @GET("crops/{userId}")
    suspend fun getHarvestedCrops(
        @Path("userId") userId: String
    ): CamstudyResponse<List<HarvestedCropDto>>

    @POST("crops")
    suspend fun plantCrop(
        @Body body: PlantCropRequestBody
    ): CamstudyResponse<Unit>

    @PATCH("crops/{userId}/{cropId}")
    suspend fun harvestCrop(
        @Path("userId") userId: String,
        @Path("cropId") cropId: String
    ): CamstudyResponse<Unit>

    @DELETE("crops/{userId}/{cropId}")
    suspend fun deleteCrop(
        @Path("userId") userId: String,
        @Path("cropId") cropId: String
    ): CamstudyResponse<Unit>
}
