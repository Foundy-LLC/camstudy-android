package io.foundy.feature.room_list.data.model

import kotlinx.datetime.Instant

data class RoomCreateRequestBody(
    val masterId: String,
    val title: String,
    val password: String?,
    val timer: Int,
    val shortBreak: Int,
    val longBreak: Int,
    val longBreakInterval: Int,
    val tags: List<String>,
    val expiredAt: String,
) {
    init {
        Instant.parse(expiredAt)
    }
}
