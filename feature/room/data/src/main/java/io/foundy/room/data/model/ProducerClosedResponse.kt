package io.foundy.room.data.model

@kotlinx.serialization.Serializable
data class ProducerClosedResponse(
    val remoteProducerId: String
)
