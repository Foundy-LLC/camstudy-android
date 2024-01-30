package io.foundy.feature.room.ui.mapper

import io.foundy.core.model.UserOverview
import io.foundy.feature.room.data.model.RoomJoiner

fun RoomJoiner.toUserOverviewWithoutIntroduce() = UserOverview(
    id = id,
    name = name,
    profileImage = profileImage,
    introduce = null
)
