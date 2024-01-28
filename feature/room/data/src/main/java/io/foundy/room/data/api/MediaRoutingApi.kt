package io.foundy.room.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.room.data.model.MediaServer
import retrofit2.http.GET
import retrofit2.http.Path

interface MediaRoutingApi {

    @GET("rooms/{roomId}/media-server")
    suspend fun getMediaServer(
        @Path("roomId") roomId: String
    ): CamstudyResponse<MediaServer>
}
