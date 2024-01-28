package io.foundy.room_list.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.room_list.data.model.GetRecommendedRoomResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface RecommendRoomApi {

    @GET("users/{userId}/recommended-rooms")
    suspend fun getRecommendedRoom(
        @Path("userId") userId: String
    ): CamstudyResponse<GetRecommendedRoomResponse>
}
