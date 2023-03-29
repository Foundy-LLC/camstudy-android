package io.foundy.room_list.ui

import androidx.annotation.StringRes

sealed class RoomListSideEffect {

    object SuccessToCreateRoom : RoomListSideEffect()

    data class Message(val content: String?, @StringRes val defaultRes: Int) : RoomListSideEffect()
}
