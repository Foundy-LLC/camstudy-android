package io.foundy.room_list.ui

import androidx.annotation.StringRes
import io.foundy.core.model.RoomOverview

sealed class RoomListSideEffect {

    data class SuccessToCreateRoom(val createdRoom: RoomOverview) : RoomListSideEffect()

    data class Message(val content: String?, @StringRes val defaultRes: Int) : RoomListSideEffect()
}
