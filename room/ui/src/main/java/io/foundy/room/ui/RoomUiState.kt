package io.foundy.room.ui

import androidx.annotation.StringRes
import com.example.domain.PeerState
import com.example.domain.PomodoroTimerProperty
import com.example.domain.PomodoroTimerState
import io.foundy.room.data.model.WaitingRoomData
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

sealed class RoomUiState {

    sealed class WaitingRoom : RoomUiState() {

        object Loading : WaitingRoom()

        data class FailedToConnect(@StringRes val messageRes: Int) : WaitingRoom()

        data class Connected(
            val currentUserId: String,
            val data: WaitingRoomData,
            val passwordInput: String = "",
            val onPasswordChange: (String) -> Unit,
            val onJoinClick: (
                localVideo: VideoTrack?,
                localAudio: AudioTrack?,
                password: String
            ) -> Unit
        ) : WaitingRoom() {
            val isCurrentUserMaster: Boolean get() = data.masterId == currentUserId
            val isFull: Boolean get() = data.joinerList.size >= data.capacity
            val isCurrentUserBlocked: Boolean get() = data.blacklist.contains(currentUserId)
        }

        val enableJoinButton: Boolean get() = cannotJoinMessage == null

        @get:StringRes
        val cannotJoinMessage: Int?
            get() {
                when (this) {
                    Loading -> return R.string.loading
                    is FailedToConnect -> return messageRes
                    is Connected -> {
                        if (data.hasPassword && passwordInput.isEmpty()) {
                            return R.string.input_password
                        }
                        if (isCurrentUserMaster) {
                            return null
                        }
                        if (isFull) {
                            return R.string.room_is_full
                        }
                        if (isCurrentUserBlocked) {
                            return R.string.cannot_join_because_blocked
                        }
                        return null
                    }
                }
            }
    }

    data class StudyRoom(
        val peerStates: List<PeerState>,
        val pomodoroTimer: PomodoroTimerProperty,
        val pomodoroTimerState: PomodoroTimerState
    ) : RoomUiState()
}
