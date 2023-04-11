package io.foundy.room.ui.viewmodel

import androidx.annotation.StringRes
import io.foundy.room.data.model.WaitingRoomData
import io.foundy.room.domain.ChatMessage
import io.foundy.room.domain.PeerOverview
import io.foundy.room.domain.PomodoroTimerProperty
import io.foundy.room.domain.PomodoroTimerState
import io.foundy.room.ui.R
import io.foundy.room.ui.peer.PeerUiState
import kotlinx.datetime.LocalDateTime
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

sealed class RoomUiState {

    sealed class WaitingRoom : RoomUiState() {

        object Loading : WaitingRoom()

        data class FailedToConnect(@StringRes val messageRes: Int) : WaitingRoom()

        object NotExists : WaitingRoom()

        data class Connected(
            val currentUserId: String,
            val data: WaitingRoomData,
            val passwordInput: String = "",
            val joining: Boolean = false,
            val onPasswordChange: (String) -> Unit,
            val onJoinClick: (
                localVideo: VideoTrack?,
                localAudio: AudioTrack?,
                password: String
            ) -> Unit
        ) : WaitingRoom() {
            val isCurrentUserMaster: Boolean get() = data.masterId == currentUserId
            val isFull: Boolean get() = data.joinerList.size >= data.capacity
            val isCurrentUserBlocked: Boolean get() = data.blacklist.any { it.id == currentUserId }
        }

        val enableJoinButton: Boolean
            get() {
                val hasErrorMessage = cannotJoinMessage != null
                if (this is Connected) {
                    return !hasErrorMessage && !joining
                }
                return !hasErrorMessage
            }

        @get:StringRes
        val joinButtonTextRes: Int
            get() {
                if (this is Connected && joining) {
                    return R.string.joining
                }
                return R.string.join
            }

        @get:StringRes
        val cannotJoinMessage: Int?
            get() {
                when (this) {
                    Loading -> return R.string.loading
                    NotExists -> return R.string.not_exists_study_room
                    is FailedToConnect -> return messageRes
                    is Connected -> {
                        if (data.joinerList.any { it.id == currentUserId }) {
                            return R.string.already_joined
                        }
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
        val peerStates: List<PeerUiState>,
        val isCurrentUserMaster: Boolean,
        val isCurrentUserKicked: Boolean = false,
        val isPipMode: Boolean = false,

        // Master's features
        val blacklist: List<PeerOverview>,
        val onKickUserClick: (userId: String) -> Unit,
        val onBlockUserClick: (userId: String) -> Unit,
        val onUnblockUserClick: (userId: String) -> Unit,
        val onSavePomodoroTimerClick: (PomodoroTimerProperty) -> Unit,

        // Chatting
        val chatMessageInput: String = "",
        val onChatMessageInputChange: (String) -> Unit,
        val onSendChatClick: (String) -> Unit,
        val chatMessages: List<ChatMessage> = emptyList(),

        // Pomodoro Timer
        val pomodoroTimerEventDate: LocalDateTime?,
        val pomodoroTimer: PomodoroTimerProperty,
        val pomodoroTimerState: PomodoroTimerState,
        val onStartPomodoroClick: () -> Unit,
    ) : RoomUiState()
}
