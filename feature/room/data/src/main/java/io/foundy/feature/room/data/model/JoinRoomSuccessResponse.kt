package io.foundy.feature.room.data.model

import io.foundy.feature.room.domain.PeerState
import io.foundy.feature.room.domain.PomodoroTimerProperty
import io.foundy.feature.room.domain.PomodoroTimerState
import io.foundy.feature.room.domain.WebRtcServerTimeZone
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
    val timerStartedDateTime: LocalDateTime? = run {
        if (timerStartedDate == null) {
            return@run null
        }
        val instant = Instant.parse(timerStartedDate)
        return@run instant.toLocalDateTime(WebRtcServerTimeZone)
    }
}
