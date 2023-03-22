package io.foundy.friend.data.api

import io.foundy.core.data.model.ResponseBody
import io.foundy.friend.data.model.UserOverviewDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendApi {

    @GET("users/{userId}/friends")
    suspend fun getFriends(
        @Path("userId") userId: String,
        @Query("page") page: Int,
        @Query("accepted") accepted: Boolean
    ): Response<ResponseBody<List<UserOverviewDto>>>
}
