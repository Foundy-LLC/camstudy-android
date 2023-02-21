package io.foundy.room_list.data.api

import io.foundy.core.data.model.ResponseBody
import io.foundy.room_list.data.model.RoomOverviewDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RoomListApi {

    @GET("rooms")
    suspend fun getRooms(
        @Query("page") page: Int = 0
    ): Response<ResponseBody<List<RoomOverviewDto>>>
}
