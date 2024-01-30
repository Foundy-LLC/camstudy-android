package io.foundy.feature.room.data.model

@kotlinx.serialization.Serializable
data class CreateWebRtcTransportRequest(
    val isConsumer: Boolean
)
