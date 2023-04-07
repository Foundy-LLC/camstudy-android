package io.foundy.room_list.data.model

import io.foundy.core.model.RoomOverview
import io.foundy.friend.data.model.UserOverviewDto
import io.foundy.friend.data.model.toEntity

data class RoomOverviewDto(
    val id: String,
    val title: String,
    val masterId: String,
    val hasPassword: Boolean,
    val thumbnail: String?,
    val joinCount: Int,
    val joinedUsers: List<UserOverviewDto>,
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
    joinedUsers = joinedUsers.map { it.toEntity() },
    maxCount = maxCount,
    tags = tags
)
