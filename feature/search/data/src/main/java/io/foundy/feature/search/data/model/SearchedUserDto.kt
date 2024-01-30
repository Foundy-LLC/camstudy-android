package io.foundy.feature.search.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.data.model.FriendStatusDto
import io.foundy.core.data.model.toEntity
import io.foundy.core.model.SearchedUser

data class SearchedUserDto(
    val id: String,
    val name: String,
    val profileImage: String?,
    val introduce: String?,
    @SerializedName("requestHistory") val friendStatus: FriendStatusDto
)

fun SearchedUserDto.toEntity() = SearchedUser(
    id = id,
    name = name,
    introduce = introduce,
    profileImage = profileImage,
    friendStatus = friendStatus.toEntity()
)
