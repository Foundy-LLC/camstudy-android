package io.foundy.room_list.data.model

import kotlinx.datetime.Instant

data class RoomCreateRequestBody(
    val masterId: String,
    val title: String,
    val password: String?,
    val timer: Int,
    val shortBreak: Int,
    val longBreak: Int,
    val longBreakInterval: Int,
    val expiredAt: String,
) {
    init {
        Instant.parse(expiredAt)
    }
}
