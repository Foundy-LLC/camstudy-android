package io.foundy.feature.room.data.extension

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> T.toJson(): String {
    return Json.encodeToString(this)
}
