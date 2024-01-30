package io.foundy.feature.room.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JoinRoomFailureResponse(
    val type: String,
    val message: String,
)
