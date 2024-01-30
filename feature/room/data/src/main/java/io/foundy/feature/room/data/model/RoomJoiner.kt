package io.foundy.feature.room.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RoomJoiner(
    val id: String,
    val name: String,
    val profileImage: String?
)
