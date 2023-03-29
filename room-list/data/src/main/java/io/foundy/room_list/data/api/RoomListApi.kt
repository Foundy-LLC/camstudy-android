package io.foundy.room_list.data.api

import io.foundy.core.data.model.ResponseBody
import io.foundy.room_list.data.model.RoomCreateRequestBody
import io.foundy.room_list.data.model.RoomOverviewDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface RoomListApi {

    @GET("rooms")
    suspend fun getRooms(
        @Query("page") page: Int = 0
    ): Response<ResponseBody<List<RoomOverviewDto>>>

    @POST("rooms")
    suspend fun createRoom(
        @Body body: RoomCreateRequestBody
    ): Response<ResponseBody<Unit>>

    @Multipart
    @POST("rooms/{roomId}/thumbnail")
    suspend fun postRoomThumbnail(
        @Part partMap: MultipartBody.Part
    ): Response<ResponseBody<Unit>>
}
