package io.foundy.room.data.model

@kotlinx.serialization.Serializable
data class OtherPeerDisconnectedResponse(
    val disposedPeerId: String
)
