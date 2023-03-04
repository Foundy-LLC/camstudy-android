package io.foundy.room.data.service

import com.example.domain.PeerState
import io.foundy.room.data.BuildConfig
import io.foundy.room.data.extension.emit
import io.foundy.room.data.extension.emitWithPrimitiveCallBack
import io.foundy.room.data.extension.on
import io.foundy.room.data.extension.onPrimitiveCallback
import io.foundy.room.data.extension.toJson
import io.foundy.room.data.model.ConsumeErrorResponse
import io.foundy.room.data.model.ConsumeResponse
import io.foundy.room.data.model.CreateWebRtcTransportRequest
import io.foundy.room.data.model.CreateWebRtcTransportResponse
import io.foundy.room.data.model.JoinRoomFailureResponse
import io.foundy.room.data.model.JoinRoomRequest
import io.foundy.room.data.model.JoinRoomSuccessResponse
import io.foundy.room.data.model.NewProducerResponse
import io.foundy.room.data.model.OtherPeerDisconnectedResponse
import io.foundy.room.data.model.ProducerClosedResponse
import io.foundy.room.data.model.Protocol
import io.foundy.room.data.model.ReceiveTransportWrapper
import io.foundy.room.data.model.RoomEvent
import io.foundy.room.data.model.RoomJoiner
import io.foundy.room.data.model.StudyRoomEvent
import io.foundy.room.data.model.UserAndProducerId
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
import org.json.JSONObject
import org.mediasoup.droid.Consumer
import org.mediasoup.droid.Device
import org.mediasoup.droid.RecvTransport
import org.mediasoup.droid.SendTransport
import org.mediasoup.droid.Transport
import org.webrtc.AudioTrack
import org.webrtc.MediaStreamTrack
import org.webrtc.VideoTrack
import java.net.URI
import javax.inject.Inject

// TODO: 다른 피어가 헤드셋 끄는 경우 처리
// TODO: 타이머 이벤트 처리 구현
// TODO: 채팅 구현
@OptIn(ExperimentalCoroutinesApi::class)
class RoomSocketService @Inject constructor() : RoomService {

    private val logger by taggedLogger("Call:RoomSocketService")

    private val socket: Socket = Manager(URI(URL)).socket(Protocol.NAME_SPACE)

    override val eventFlow: MutableSharedFlow<RoomEvent> = MutableSharedFlow(replay = 1)

    private var _device: Device? = null
    private val device: Device get() = requireNotNull(_device)

    private var didGetInitProducers = false

    private var _sendTransport: SendTransport? = null
    private val sendTransport: SendTransport get() = requireNotNull(_sendTransport)

    private val receiveTransportWrappers: MutableList<ReceiveTransportWrapper> = mutableListOf()

    override suspend fun connect() = suspendCoroutineWithTimeout { continuation ->
        socket.run {
            connect()

            on(Protocol.CONNECTION_SUCCESS) {
                logger.d { "Connected socket server." }
                off(Protocol.CONNECTION_SUCCESS)
                continuation.resume(Unit) {}
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
                eventFlow.tryEmit(WaitingRoomEvent.OtherPeerJoined(joiner = joiner))
            }
            onPrimitiveCallback(Protocol.OTHER_PEER_EXITED_ROOM) { userId: String ->
                logger.d { "Exited other peer from room: $userId" }
                eventFlow.tryEmit(WaitingRoomEvent.OtherPeerExited(userId = userId))
            }
        }
    }

    private fun removeWaitingRoomEventListeners() {
        socket.off(Protocol.OTHER_PEER_JOINED_ROOM)
        socket.off(Protocol.OTHER_PEER_EXITED_ROOM)
    }

    override suspend fun joinToStudyRoom(
        localVideo: VideoTrack?,
        localAudio: AudioTrack?,
        userId: String,
        password: String
    ): Result<JoinRoomSuccessResponse> = suspendCoroutineWithTimeout { continuation ->
        removeWaitingRoomEventListeners()
        socket.emit(
            Protocol.JOIN_ROOM,
            arg = JSONObject(
                JoinRoomRequest(
                    userId = userId,
                    // TODO: 음소거 기능 구현되면 초기값 전달하도록 수정하기
                    mutedHeadset = false,
                    roomPasswordInput = password
                ).toJson()
            ),
            onSuccess = { response: JoinRoomSuccessResponse ->
                logger.d { response.toString() }
                _device = Device().apply { load(response.rtpCapabilities.toString()) }
                createSendTransport(localVideo, localAudio)
                listenRoomEvents(currentUserId = userId)
                val responseThatFilteredCurrentUser = response.copy(
                    peerStates = response.peerStates.filter { it.uid != userId }
                )
                continuation.resume(Result.success(responseThatFilteredCurrentUser)) {}
            },
            onFailure = { response: JoinRoomFailureResponse ->
                logger.e { response.toString() }
                continuation.resume(Result.failure(Exception(response.message))) {}
            }
        )
    }

    override suspend fun produceVideo(videoTrack: VideoTrack) {
        sendTransport.produce({ logger.d { "Video onTransportClose" } }, videoTrack, null, null)
    }

    override suspend fun closeVideoProducer() {
        socket.emit(Protocol.CLOSE_VIDEO_PRODUCER)
    }

    override suspend fun produceAudio(audioTrack: AudioTrack) {
        sendTransport.produce({ logger.d { "Audio onTransportClose" } }, audioTrack, null, null)
    }

    override suspend fun closeAudioProducer() {
        socket.emit(Protocol.CLOSE_AUDIO_PRODUCER)
    }

    private fun listenRoomEvents(currentUserId: String) = with(socket) {
        on(Protocol.PEER_STATE_CHANGED) { state: PeerState ->
            if (state.uid == currentUserId) {
                return@on
            }
            eventFlow.tryEmit(StudyRoomEvent.OnChangePeerState(state = state))
        }
        on(Protocol.NEW_PRODUCER) { response: NewProducerResponse ->
            createReceiveTransportAndConsume(
                userId = response.userId,
                remoteProducerId = response.producerId
            )
        }
        on(Protocol.PRODUCER_CLOSED) { response: ProducerClosedResponse ->
            val transportWrapper = receiveTransportWrappers.find {
                it.producerId == response.remoteProducerId
            }
            if (transportWrapper != null) {
                transportWrapper.consumer.close()
                val userId = transportWrapper.userId
                val event = when (transportWrapper.consumer.kind) {
                    MediaStreamTrack.VIDEO_TRACK_KIND -> StudyRoomEvent.OnCloseVideoConsumer(
                        userId = userId
                    )
                    MediaStreamTrack.AUDIO_TRACK_KIND -> StudyRoomEvent.OnCloseAudioConsumer(
                        userId = userId
                    )
                    else -> throw IllegalArgumentException()
                }
                eventFlow.tryEmit(event)
            }
        }
        on(Protocol.OTHER_PEER_DISCONNECTED) { response: OtherPeerDisconnectedResponse ->
            val disposedPeerId = response.disposedPeerId
            receiveTransportWrappers.removeAll { it.userId == disposedPeerId }
            eventFlow.tryEmit(StudyRoomEvent.OnDisconnectPeer(disposedPeerId = disposedPeerId))
        }
    }

    private fun createSendTransport(localVideo: VideoTrack?, localAudio: AudioTrack?) {
        socket.emit(
            Protocol.CREATE_WEB_RTC_TRANSPORT,
            JSONObject(CreateWebRtcTransportRequest(isConsumer = false).toJson()),
        ) { response: CreateWebRtcTransportResponse ->
            _sendTransport = device.createSendTransport(
                sendTransportListener,
                response.id,
                response.iceParameters.toString(),
                response.iceCandidates.toString(),
                response.dtlsParameters.toString()
            )

            produceLocalMedia(localVideo, localAudio)
        }
    }

    private fun produceLocalMedia(
        localVideo: VideoTrack?,
        localAudio: AudioTrack?
    ) {
        localVideo?.let {
            sendTransport.produce(
                { logger.d { "Video onTransportClose" } },
                it,
                null,
                null
            )
        }
        localAudio?.let {
            sendTransport.produce(
                { logger.d { "Audio onTransportClose" } },
                it,
                null,
                null
            )
        }
    }

    private val sendTransportListener = object : SendTransport.Listener {

        override fun onConnect(transport: Transport, dtlsParameters: String) {
            logger.d { "SendTransport.Listener.onConnect: $transport" }
            socket.emit(Protocol.TRANSPORT_PRODUCER_CONNECT, JSONObject(dtlsParameters))
        }

        override fun onConnectionStateChange(transport: Transport, connectionState: String) {
            logger.d { "onConnectionStateChange: $connectionState" }
        }

        override fun onProduce(
            transport: Transport,
            kind: String,
            rtpParameters: String,
            appData: String
        ): String {
            socket.emitWithPrimitiveCallBack(
                Protocol.TRANSPORT_PRODUCER,
                JSONObject(
                    mapOf(
                        "kind" to kind,
                        "rtpParameters" to JSONObject(rtpParameters),
                        "appData" to JSONObject(appData)
                    )
                )
            ) { _: String, producersExists: Boolean ->
                if (!didGetInitProducers && producersExists) {
                    didGetInitProducers = true
                    getRemoteProducersAndCreateReceiveTransport()
                }
            }
            return transport.id
        }
    }

    private fun getRemoteProducersAndCreateReceiveTransport() {
        socket.emit(Protocol.GET_PRODUCER_IDS) { userAndProducerIds: List<UserAndProducerId> ->
            logger.d { "Got remote producers: $userAndProducerIds" }
            for (idSet in userAndProducerIds) {
                createReceiveTransportAndConsume(
                    userId = idSet.userId,
                    remoteProducerId = idSet.producerId
                )
            }
        }
    }

    private fun createReceiveTransportAndConsume(userId: String, remoteProducerId: String) {
        val wrapper = receiveTransportWrappers.find { it.userId == userId }
        if (wrapper != null) {
            consumeReceiveTransport(
                receiveTransport = wrapper.transport,
                serverReceiveTransportId = wrapper.serverReceiveTransportId,
                remoteProducerId = remoteProducerId,
                userId = wrapper.userId
            )
            return
        }
        socket.emit(
            Protocol.CREATE_WEB_RTC_TRANSPORT,
            JSONObject(CreateWebRtcTransportRequest(isConsumer = true).toJson()),
        ) { response: CreateWebRtcTransportResponse ->
            val receiveTransport = device.createRecvTransport(
                receiveTransportListener,
                response.id,
                response.iceParameters.toString(),
                response.iceCandidates.toString(),
                response.dtlsParameters.toString(),
                "{}",
            )
            consumeReceiveTransport(
                receiveTransport = receiveTransport,
                serverReceiveTransportId = response.id,
                remoteProducerId = remoteProducerId,
                userId = userId
            )
        }
    }

    private val receiveTransportListener = object : RecvTransport.Listener {

        override fun onConnect(transport: Transport, dtlsParameters: String) {
            logger.d { "RecvTransport.Listener.onConnect: $transport" }
            socket.emit(
                Protocol.TRANSPORT_RECEIVER_CONNECT,
                JSONObject(
                    mapOf(
                        "dtlsParameters" to JSONObject(dtlsParameters),
                        "serverReceiveTransportId" to transport.id
                    )
                )
            )
        }

        override fun onConnectionStateChange(transport: Transport, connectionState: String) {
            logger.d { "onConnectionStateChange: $connectionState" }
        }
    }

    private fun consumeReceiveTransport(
        receiveTransport: RecvTransport,
        serverReceiveTransportId: String,
        remoteProducerId: String,
        userId: String
    ) {
        logger.d { "Try to consume from server: $userId" }
        socket.emit(
            Protocol.CONSUME,
            JSONObject(
                mapOf(
                    "rtpCapabilities" to JSONObject(device.rtpCapabilities),
                    "remoteProducerId" to remoteProducerId,
                    "serverReceiveTransportId" to serverReceiveTransportId
                )
            ),
            onSuccess = { response: ConsumeResponse ->
                logger.d { "Success to consume from server: ${response.id}" }
                val consumer: Consumer = receiveTransport.consume(
                    { logger.d { "RecvTransport.onTransportClose" } },
                    response.id,
                    response.producerId,
                    response.kind,
                    response.rtpParameters.toString(),
                    "{}"
                )
                val wrapper = ReceiveTransportWrapper(
                    transport = receiveTransport,
                    serverReceiveTransportId = serverReceiveTransportId,
                    producerId = response.producerId,
                    userId = userId,
                    consumer = consumer
                )
                receiveTransportWrappers.add(wrapper)

                eventFlow.tryEmit(
                    StudyRoomEvent.AddedConsumer(
                        userId = userId,
                        track = consumer.track
                    )
                )

                socket.emit(Protocol.CONSUME_RESUME, response.serverConsumerId)
            },
            onFailure = { error: ConsumeErrorResponse ->
                logger.e { "Failed to consume from server: ${error.error}" }
            }
        )
    }

    override fun disconnect() {
        socket.disconnect()
    }

    companion object {
        private const val URL = "http://10.0.2.2:2000"

        private val TimeOutMilli = if (BuildConfig.DEBUG) 20_000L else 5_000L

        private suspend inline fun <T> suspendCoroutineWithTimeout(
            crossinline block: (CancellableContinuation<T>) -> Unit
        ): T {
            return withTimeout(TimeOutMilli) {
                suspendCancellableCoroutine(block)
            }
        }
    }
}
