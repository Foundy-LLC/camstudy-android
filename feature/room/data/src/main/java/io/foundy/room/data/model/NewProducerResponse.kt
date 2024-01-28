package io.foundy.room.data.model

@kotlinx.serialization.Serializable
data class NewProducerResponse(
    val producerId: String,
    val userId: String
)
