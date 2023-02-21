package io.foundy.room_list.data.model

import io.foundy.core.model.RoomOverview

data class RoomOverviewDto(
    val id: String,
    val title: String,
    val masterId: String,
    val hasPassword: Boolean,
    val thumbnail: String?,
    val joinCount: Int,
    val maxCount: Int,
    val tags: List<String>
)

fun RoomOverviewDto.toEntity(): RoomOverview = RoomOverview(
    id = id,
    title = title,
    masterId = masterId,
    hasPassword = hasPassword,
    thumbnail = thumbnail,
    joinCount = joinCount,
    maxCount = maxCount,
    tags = tags
)
