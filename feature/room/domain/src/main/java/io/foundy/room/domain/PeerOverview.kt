package io.foundy.room.domain

@kotlinx.serialization.Serializable
data class PeerOverview(
    val id: String,
    val name: String,
)
