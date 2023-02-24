package io.foundy.room.data.model

import com.example.domain.PeerState
import com.example.domain.PomodoroTimerProperty
import com.example.domain.PomodoroTimerState
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
data class JoinRoomSuccessResponse(
    val type: String,
    val rtpCapabilities: JsonObject,
    val peerStates: List<PeerState>,
    val timerStartedDate: LocalDateTime? = null,
    val timerState: PomodoroTimerState,
    val timerProperty: PomodoroTimerProperty
)
