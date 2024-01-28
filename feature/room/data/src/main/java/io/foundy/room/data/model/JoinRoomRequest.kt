package io.foundy.room.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JoinRoomRequest(
    val userId: String,
    val mutedHeadset: Boolean,
    val roomPasswordInput: String
)
