package io.foundy.room.ui.mapper

import io.foundy.core.model.UserOverview
import io.foundy.room.data.model.RoomJoiner

fun RoomJoiner.toUserOverviewWithoutIntroduce() = UserOverview(
    id = id,
    name = name,
    profileImage = profileImage,
    introduce = null
)
