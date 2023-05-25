package io.foundy.room.ui.room

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.foundy.core.model.constant.RoomConstants
import io.foundy.room.data.model.WaitingRoomData
import io.foundy.room.domain.PeerOverview
import io.foundy.room.domain.PomodoroTimerProperty
import io.foundy.room.domain.PomodoroTimerState
import io.foundy.room.ui.R
import io.foundy.room.ui.model.ChatMessageUiState
import io.foundy.room.ui.peer.PeerUiState
import kotlinx.datetime.LocalDateTime
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

sealed class RoomUiState {

    sealed class WaitingRoom : RoomUiState() {

        object Loading : WaitingRoom()

        data class FailedToConnect(
            val message: String? = null,
            @StringRes val defaultMessageRes: Int
        ) : WaitingRoom()

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

        @Composable
        fun isEnabledJoinButton(): Boolean {
            val hasErrorMessage = getCannotJoinMessage() != null
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

        @Composable
        fun getCannotJoinMessage(): String? {
            return when (this) {
                Loading -> return stringResource(R.string.loading)
                NotExists -> return stringResource(R.string.not_exists_study_room)
                is FailedToConnect -> return message ?: stringResource(defaultMessageRes)
                is Connected -> {
                    val passwordNeeded = data.hasPassword &&
                        passwordInput.length < RoomConstants.PasswordRange.first
                    val alreadyJoined = data.joinerList.any { it.id == currentUserId }

                    if (isCurrentUserMaster) {
                        if (data.joinerList.any { it.id == currentUserId }) {
                            return stringResource(R.string.already_joined)
                        }
                        if (passwordNeeded) {
                            return stringResource(R.string.input_password)
                        }
                        return null
                    }

                    if (isCurrentUserBlocked) {
                        return stringResource(R.string.cannot_join_because_blocked)
                    }
                    if (alreadyJoined) {
                        return stringResource(R.string.already_joined)
                    }
                    if (isFull) {
                        return stringResource(R.string.room_is_full)
                    }
                    if (passwordNeeded) {
                        return stringResource(R.string.input_password)
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
        val chatMessages: List<ChatMessageUiState> = emptyList(),

        // Pomodoro Timer
        val pomodoroTimerEventDate: LocalDateTime?,
        val pomodoroTimer: PomodoroTimerProperty,
        val pomodoroTimerState: PomodoroTimerState,
        val onStartPomodoroClick: () -> Unit,
    ) : RoomUiState()
}
