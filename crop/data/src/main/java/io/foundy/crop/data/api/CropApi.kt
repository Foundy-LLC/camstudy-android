package io.foundy.crop.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.crop.data.model.GrowingCropDto
import io.foundy.crop.data.model.PlantCropRequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CropApi {

    @GET("crops/{userId}/growing")
    suspend fun getGrowingCrop(
        @Path("userId") userId: String
    ): CamstudyResponse<GrowingCropDto>

    @POST("crops")
    suspend fun plantCrop(
        @Body body: PlantCropRequestBody
    ): CamstudyResponse<Unit>
}
