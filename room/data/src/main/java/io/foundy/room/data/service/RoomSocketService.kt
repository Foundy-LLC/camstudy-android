package io.foundy.room.data.service

import android.util.Log
import io.foundy.room.data.extension.emit
import io.foundy.room.data.extension.on
import io.foundy.room.data.model.Protocol
import io.foundy.room.data.model.RoomEvent
import io.foundy.room.data.model.RoomJoiner
import io.foundy.room.data.model.WaitingRoomData
import io.foundy.room.data.model.WaitingRoomEvent
import io.socket.client.Manager
import io.socket.client.Socket
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.net.URI
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class RoomSocketService @Inject constructor() : RoomService {

    private val socket: Socket = Manager(URI(URL)).socket(Protocol.NAME_SPACE)

    override val event: MutableSharedFlow<RoomEvent> = MutableSharedFlow(replay = 1)

    override suspend fun connect() = suspendCoroutineWithTimeout { continuation ->
        socket.run {
            connect()

            on(Protocol.CONNECTION_SUCCESS) {
                Log.d(TAG, "Connected socket server.")
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
            Log.d(TAG, "Joined waiting room: $roomId")
            listenWaitingRoomEvents()
            continuation.resume(waitingRoomData) {}
        }
    }

    private fun listenWaitingRoomEvents() {
        socket.run {
            on(Protocol.OTHER_PEER_JOINED_ROOM) { joiner: RoomJoiner ->
                Log.d(TAG, "Joined other peer in room: $joiner")
                event.tryEmit(WaitingRoomEvent.OtherPeerJoined(joiner = joiner))
            }
            on(Protocol.OTHER_PEER_EXITED_ROOM) { userId: String ->
                Log.d(TAG, "Exited other peer from room: $userId")
                event.tryEmit(WaitingRoomEvent.OtherPeerExited(userId = userId))
            }
        }
    }

    private fun removeWaitingRoomEventsListener() {
        socket.off(Protocol.OTHER_PEER_JOINED_ROOM)
        socket.off(Protocol.OTHER_PEER_EXITED_ROOM)
    }

    companion object {
        private const val URL = "http://10.0.2.2:2000"
        private const val TAG = "RoomSocketService"

        private suspend inline fun <T> suspendCoroutineWithTimeout(
            crossinline block: (CancellableContinuation<T>) -> Unit
        ): T {
            return withTimeout(5_000L) {
                suspendCancellableCoroutine(block)
            }
        }
    }
}
