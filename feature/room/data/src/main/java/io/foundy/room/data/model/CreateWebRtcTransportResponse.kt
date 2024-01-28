package io.foundy.room.data.model

import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
data class CreateWebRtcTransportResponse(
    val id: String,
    val iceParameters: JsonObject,
    val iceCandidates: List<JsonObject>,
    val dtlsParameters: JsonObject
)
