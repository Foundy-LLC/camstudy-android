package io.foundy.room.data.service

import io.foundy.room.data.extension.emit
import io.foundy.room.data.extension.on
import io.foundy.room.data.model.JoinRoomFailureResponse
import io.foundy.room.data.model.JoinRoomRequestArgument
import io.foundy.room.data.model.JoinRoomSuccessResponse
import io.foundy.room.data.model.Protocol
import io.foundy.room.data.model.RoomEvent
import io.foundy.room.data.model.RoomJoiner
import io.foundy.room.data.model.WaitingRoomData
import io.foundy.room.data.model.WaitingRoomEvent
import io.getstream.log.taggedLogger
import io.socket.client.Manager
import io.socket.client.Socket
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.webrtc.AudioTrack
import org.webrtc.VideoTrack
import java.net.URI
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class RoomSocketService @Inject constructor() : RoomService {

    private val logger by taggedLogger("Call:RoomSocketService")

    private val socket: Socket = Manager(URI(URL)).socket(Protocol.NAME_SPACE)

    override val event: MutableSharedFlow<RoomEvent> = MutableSharedFlow(replay = 1)

    override suspend fun connect() = suspendCoroutineWithTimeout { continuation ->
        socket.run {
            connect()

            on(Protocol.CONNECTION_SUCCESS) {
                logger.d { "Connected socket server." }
                continuation.resume(Unit) {
                    off(Protocol.CONNECTION_SUCCESS)
                }
            }
        }
    }

    override suspend fun joinToWaitingRoom(
        roomId: String
    ) = suspendCoroutineWithTimeout { continuation ->
        socket.emit(Protocol.JOIN_WAITING_ROOM, roomId) { waitingRoomData: WaitingRoomData ->
            logger.d { "Joined waiting room: $roomId" }
            listenWaitingRoomEvents()
            continuation.resume(waitingRoomData) {}
        }
    }

    private fun listenWaitingRoomEvents() {
        socket.run {
            on(Protocol.OTHER_PEER_JOINED_ROOM) { joiner: RoomJoiner ->
                logger.d { "Joined other peer in room: $joiner" }
                event.tryEmit(WaitingRoomEvent.OtherPeerJoined(joiner = joiner))
            }
            on(Protocol.OTHER_PEER_EXITED_ROOM) { userId: String ->
                logger.d { "Exited other peer from room: $userId" }
                event.tryEmit(WaitingRoomEvent.OtherPeerExited(userId = userId))
            }
        }
    }

    private fun removeWaitingRoomEventsListener() {
        socket.off(Protocol.OTHER_PEER_JOINED_ROOM)
        socket.off(Protocol.OTHER_PEER_EXITED_ROOM)
    }

    override suspend fun joinToStudyRoom(
        localVideo: VideoTrack?,
        localAudio: AudioTrack?,
        userId: String,
        password: String
    ): Result<JoinRoomSuccessResponse> = suspendCoroutineWithTimeout { continuation ->
        removeWaitingRoomEventsListener()
        socket.emit(
            Protocol.JOIN_ROOM,
            arg = org.json.JSONObject(
                Json.encodeToString(
                    JoinRoomRequestArgument(
                        userId = userId,
                        roomPasswordInput = password
                    )
                )
            ),
            onSuccess = { response: JoinRoomSuccessResponse ->
                logger.d { response.toString() }
                continuation.resume(Result.success(response)) {}
            },
            onFailure = { response: JoinRoomFailureResponse ->
                logger.d { response.toString() }
                continuation.resume(Result.failure(Exception(response.message))) {}
            }
        )
    }

    companion object {
        private const val URL = "http://10.0.2.2:2000"

        private suspend inline fun <T> suspendCoroutineWithTimeout(
            crossinline block: (CancellableContinuation<T>) -> Unit
        ): T {
            return withTimeout(5_000L) {
                suspendCancellableCoroutine(block)
            }
        }
    }
}
