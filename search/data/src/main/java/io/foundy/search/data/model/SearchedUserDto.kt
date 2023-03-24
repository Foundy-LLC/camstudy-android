package io.foundy.search.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.model.SearchedUser

data class SearchedUserDto(
    val id: String,
    val name: String,
    val profileImage: String,
    @SerializedName("requestHistory") val friendStatus: FriendStatusDto
)

fun SearchedUserDto.toEntity() = SearchedUser(
    id = id,
    name = name,
    profileImage = profileImage,
    friendStatus = friendStatus.toEntity()
)
