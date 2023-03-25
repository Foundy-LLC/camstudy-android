package io.foundy.room.data.model

import io.foundy.room.domain.PeerState
import io.foundy.room.domain.PomodoroTimerProperty
import io.foundy.room.domain.PomodoroTimerState
import io.foundy.room.domain.WebRtcServerTimeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
data class JoinRoomSuccessResponse(
    val type: String,
    val rtpCapabilities: JsonObject,
    val peerStates: List<PeerState>,
    /** ISO format */
    private val timerStartedDate: String? = null,
    val timerState: PomodoroTimerState,
    val timerProperty: PomodoroTimerProperty
) {
    val timerStartedDateTime: LocalDateTime?
        get() {
            if (timerStartedDate == null) {
                return null
            }
            val instant = Instant.parse(timerStartedDate)
            return instant.toLocalDateTime(WebRtcServerTimeZone)
        }
}
