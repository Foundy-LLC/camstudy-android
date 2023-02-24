package io.foundy.room.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JoinRoomRequestArgument(
    val userId: String,
    val roomPasswordInput: String
)
