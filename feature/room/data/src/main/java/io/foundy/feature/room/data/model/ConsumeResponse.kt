package io.foundy.feature.room.data.model

import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
internal data class ConsumeResponse(
    val id: String,
    val producerId: String,
    val kind: String,
    val rtpParameters: JsonObject,
    val serverConsumerId: String,
)
