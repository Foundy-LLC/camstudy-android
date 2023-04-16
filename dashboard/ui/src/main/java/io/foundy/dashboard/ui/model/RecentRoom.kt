package io.foundy.dashboard.ui.model

import io.foundy.core.model.RoomOverview

data class RecentRoom(
    val id: String,
    val thumbnail: String?,
    val title: String,
    val tags: List<String>
)

fun RoomOverview.toRecentRoom() = RecentRoom(
    id = id,
    thumbnail = thumbnail,
    title = title,
    tags = tags
)
