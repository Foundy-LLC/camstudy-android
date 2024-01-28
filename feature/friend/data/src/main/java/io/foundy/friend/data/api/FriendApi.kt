package io.foundy.friend.data.api

import io.foundy.core.data.model.FriendStatusDto
import io.foundy.core.data.util.CamstudyResponse
import io.foundy.friend.data.model.FriendPostRequestBody
import io.foundy.friend.data.model.GetFriendsResponse
import io.foundy.friend.data.model.UserOverviewDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendApi {

    @GET("users/{userId}/friends?accepted=true")
    suspend fun getFriends(
        @Path("userId") userId: String,
        @Query("page") page: Int,
    ): CamstudyResponse<GetFriendsResponse>

    @GET("users/{userId}/friends?accepted=false")
    suspend fun getFriendRequests(
        @Path("userId") userId: String,
        @Query("page") page: Int,
    ): CamstudyResponse<List<UserOverviewDto>>

    @POST("users/{userId}/friends")
    suspend fun requestFriend(
        @Path("userId") requesterId: String,
        @Body body: FriendPostRequestBody
    ): CamstudyResponse<FriendStatusDto>

    @PUT("users/{userId}/friends/{friendId}")
    suspend fun acceptRequest(
        @Path("userId") userId: String,
        @Path("friendId") friendId: String
    ): CamstudyResponse<Unit>

    @DELETE("users/{requesterId}/friends/{acceptorId}")
    suspend fun deleteFriend(
        @Path("requesterId") requesterId: String,
        @Path("acceptorId") acceptorId: String
    ): CamstudyResponse<Unit>
}
