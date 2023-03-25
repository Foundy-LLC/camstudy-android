package io.foundy.room.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PomodoroTimerState {

    @SerialName("stopped")
    STOPPED,

    @SerialName("started")
    STARTED,

    @SerialName("shortBreak")
    SHORT_BREAK,

    @SerialName("longBreak")
    LONG_BREAK
}
