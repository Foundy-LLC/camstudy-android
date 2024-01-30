package io.foundy.feature.room.data.model

@kotlinx.serialization.Serializable
data class OtherPeerDisconnectedResponse(
    val disposedPeerId: String
)
