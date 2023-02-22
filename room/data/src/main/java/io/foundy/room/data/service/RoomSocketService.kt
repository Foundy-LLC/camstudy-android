package io.foundy.room.data.service

import android.util.Log
import io.foundy.room.data.extension.emit
import io.foundy.room.data.model.Protocol
import io.foundy.room.data.model.RoomEvent
import io.foundy.room.data.model.WaitingRoomData
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

    override val event: MutableSharedFlow<RoomEvent> = MutableSharedFlow()

    private var _socket: Socket? = null
    private val socket: Socket get() = requireNotNull(_socket)

    override suspend fun connect() = suspendCoroutineWithTimeout { continuation ->
        val manager = Manager(URI(URL))
        _socket = manager.socket(Protocol.NAME_SPACE).apply {
            connect()

            on(Protocol.CONNECTION_SUCCESS) {
                Log.d(TAG, "Connected socket server.")
                continuation.resume(Unit) {}
            }
        }
    }

    override suspend fun joinToWaitingRoom(
        roomId: String
    ) = suspendCoroutineWithTimeout { continuation ->
        socket.emit(Protocol.JOIN_WAITING_ROOM, roomId) { waitingRoomData: WaitingRoomData ->
            Log.e(TAG, "Joined waiting room: $roomId")
            continuation.resume(waitingRoomData) {}
        }
    }

    companion object {
        private const val URL = "http://10.0.2.2:2000"
        private const val TAG = "RoomSocketService"

        private suspend inline fun <T> suspendCoroutineWithTimeout(
            crossinline block: (CancellableContinuation<T>) -> Unit
        ): T {
            return withTimeout(3_000L) {
                suspendCancellableCoroutine(block)
            }
        }
    }
}
