package io.foundy.feature.room_list.ui.create

import io.foundy.core.model.RoomOverview
import io.foundy.core.ui.UserMessage

sealed class RoomCreateSideEffect {
    data class SuccessToCreate(val createdRoom: RoomOverview) : RoomCreateSideEffect()
    data class ErrorMessage(val message: UserMessage) : RoomCreateSideEffect()
}
