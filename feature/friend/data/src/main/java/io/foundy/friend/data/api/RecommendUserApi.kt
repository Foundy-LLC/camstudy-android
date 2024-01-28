package io.foundy.friend.data.api

import io.foundy.core.data.util.CamstudyResponse
import io.foundy.friend.data.model.GetRecommendedFriendsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface RecommendUserApi {

    @GET("users/{userId}/recommended-friends")
    suspend fun getRecommendedFriends(
        @Path("userId") userId: String
    ): CamstudyResponse<GetRecommendedFriendsResponse>
}
