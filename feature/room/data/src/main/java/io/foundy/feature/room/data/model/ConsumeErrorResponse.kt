package io.foundy.feature.room.data.model

import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
internal data class ConsumeErrorResponse(
    val error: JsonObject
)
