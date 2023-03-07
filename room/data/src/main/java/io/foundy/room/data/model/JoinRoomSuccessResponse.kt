package io.foundy.room.data.model

import com.example.domain.PeerState
import com.example.domain.PomodoroTimerProperty
import com.example.domain.PomodoroTimerState
import com.example.domain.WebRtcServerTimeZone
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
