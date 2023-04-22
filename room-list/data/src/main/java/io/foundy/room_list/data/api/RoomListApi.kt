package io.foundy.room_list.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.room_list.data.model.RoomCreateRequestBody
import io.foundy.room_list.data.model.RoomOverviewDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RoomListApi {

    @GET("rooms")
    suspend fun getRooms(
        @Query("page") page: Int = 0,
        @Query("query") query: String,
    ): CamstudyResponse<List<RoomOverviewDto>>

    @GET("users/{userId}/recent-rooms")
    suspend fun getRecentRooms(
        @Path("userId") userId: String
    ): CamstudyResponse<List<RoomOverviewDto>>

    @POST("rooms")
    suspend fun createRoom(
        @Body body: RoomCreateRequestBody
    ): CamstudyResponse<Unit>

    @Multipart
    @POST("rooms/{roomId}/thumbnail")
    suspend fun postRoomThumbnail(
        @Part partMap: MultipartBody.Part
    ): CamstudyResponse<Unit>
}
