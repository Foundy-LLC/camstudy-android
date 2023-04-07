package io.foundy.core.model

data class RoomOverview(
    val id: String,
    val title: String,
    val masterId: String,
    val hasPassword: Boolean,
    val thumbnail: String?,
    val joinCount: Int,
    val joinedUsers: List<UserOverview>,
    val maxCount: Int,
    val tags: List<String>
)
