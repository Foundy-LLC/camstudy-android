package io.foundy.room.data.model

@kotlinx.serialization.Serializable
data class UserAndProducerId(
    val producerId: String,
    val userId: String
)
