package io.foundy.core.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.model.FriendStatus

enum class FriendStatusDto {

    @SerializedName("NONE")
    NONE,

    @SerializedName("REQUESTED")
    REQUESTED,

    @SerializedName("REQUEST_RECEIVED")
    REQUEST_RECEIVED,

    @SerializedName("ACCEPTED")
    ACCEPTED
}

fun FriendStatusDto.toEntity(): FriendStatus {
    return when (this) {
        FriendStatusDto.NONE -> FriendStatus.NONE
        FriendStatusDto.REQUESTED -> FriendStatus.REQUESTED
        FriendStatusDto.REQUEST_RECEIVED -> FriendStatus.REQUEST_RECEIVED
        FriendStatusDto.ACCEPTED -> FriendStatus.ACCEPTED
    }
}
