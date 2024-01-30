package io.foundy.feature.room.data.service

import android.annotation.SuppressLint
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.feature.room.data.BuildConfig
import io.foundy.feature.room.data.api.MediaRoutingApi
import io.foundy.feature.room.data.extension.emit
import io.foundy.feature.room.data.extension.emitWithPrimitiveCallBack
import io.foundy.feature.room.data.extension.on
import io.foundy.feature.room.data.extension.onPrimitiveCallback
import io.foundy.feature.room.data.extension.toJson
import io.foundy.feature.room.data.model.ConsumeErrorResponse
import io.foundy.feature.room.data.model.ConsumeResponse
import io.foundy.feature.room.data.model.CreateWebRtcTransportRequest
import io.foundy.feature.room.data.model.CreateWebRtcTransportResponse
import io.foundy.feature.room.data.model.JoinRoomFailureResponse
import io.foundy.feature.room.data.model.JoinRoomRequest
import io.foundy.feature.room.data.model.JoinRoomSuccessResponse
import io.foundy.feature.room.data.model.NewProducerResponse
import io.foundy.feature.room.data.model.OtherPeerDisconnectedResponse
import io.foundy.feature.room.data.model.ProducerClosedResponse
import io.foundy.feature.room.data.model.Protocol
import io.foundy.feature.room.data.model.ReceiveTransportWrapper
import io.foundy.feature.room.data.model.RoomEvent
import io.foundy.feature.room.data.model.RoomJoiner
import io.foundy.feature.room.data.model.StudyRoomEvent
import io.foundy.feature.room.data.model.UserAndProducerId
import io.foundy.feature.room.data.model.WaitingRoomData
import io.foundy.feature.room.data.model.WaitingRoomEvent
import io.foundy.feature.room.domain.ChatMessage
import io.foundy.feature.room.domain.PeerState
import io.foundy.feature.room.domain.PomodoroTimerProperty
import io.foundy.feature.room.domain.PomodoroTimerState
import io.getstream.log.taggedLogger
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
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
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@OptIn(ExperimentalCoroutinesApi::class)
class RoomSocketService @Inject constructor(
    private val mediaRoutingApi: MediaRoutingApi
) : RoomService {

    private val logger by taggedLogger("Call:RoomSocketService")

    private var _socket: Socket? = null
    private val socket: Socket get() = requireNotNull(_socket)

    override val eventFlow: MutableSharedFlow<RoomEvent> = MutableSharedFlow(
        extraBufferCapacity = 8
    )

    private var _device: Device? = null
    private val device: Device get() = requireNotNull(_device)

    private var _sendTransport: SendTransport? = null
    private val sendTransport: SendTransport get() = requireNotNull(_sendTransport)

    private val receiveTransportWrappers: MutableList<ReceiveTransportWrapper> = mutableListOf()

    private var mutedHeadset: Boolean = false

    // TODO: 발급 받은 SSL 인증서로 접속하도록 수정
    @SuppressLint("CustomX509TrustManager")
    private fun buildSocketOptions(): IO.Options {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, null)
        return IO.Options().apply {
            this.secure = true
            this.callFactory = OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
                .hostnameVerifier { _, _ -> true }
                .sslSocketFactory(
                    sslSocketFactory = sslContext.socketFactory,
                    trustManager = trustAllCerts[0] as X509TrustManager
                )
                .build()
        }
    }

    override suspend fun connect(roomId: String) {
        val response = mediaRoutingApi.getMediaServer(roomId = roomId)
        val url = response.getDataOrThrowMessage().url

        _socket = Manager(URI(url), buildSocketOptions()).socket(Protocol.NAME_SPACE)

        suspendCoroutineWithTimeout { continuation ->
            socket.run {
                on(Socket.EVENT_CONNECT) {
                    logger.d { "Connected to socket server." }
                }

                on(Socket.EVENT_CONNECT_ERROR) { error ->
                    logger.e { "Socket connection error: $error" }
                }

                on(Socket.EVENT_DISCONNECT) { args ->
                    logger.e { "Disconnected socket: $args" }
                    eventFlow.tryEmit(StudyRoomEvent.DisconnectedSocket)
                    disconnect()
                }

                on(Protocol.CONNECTION_SUCCESS) {
                    logger.d { "Connected socket server." }
                    off(Protocol.CONNECTION_SUCCESS)
                    continuation.resume(Unit) {}
                }

                connect()
            }
        }
    }

    override suspend fun joinToWaitingRoom(
        roomId: String
    ) = suspendCoroutineWithTimeout { continuation ->
        socket.emit(Protocol.JOIN_WAITING_ROOM, roomId) { waitingRoomData: WaitingRoomData? ->
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
                    mutedHeadset = mutedHeadset,
                    roomPasswordInput = password
                ).toJson()
            ),
            onSuccess = { response: JoinRoomSuccessResponse ->
                logger.d { response.toString() }
                _device = Device().apply { load(response.rtpCapabilities.toString()) }
                createSendTransport(localVideo, localAudio)
                listenRoomEvents(currentUserId = userId)
                getRemoteProducersAndCreateReceiveTransport()
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

    override suspend fun muteHeadset() {
        mutedHeadset = true
        receiveTransportWrappers.removeAll { wrapper ->
            if (wrapper.consumer.kind == MediaStreamTrack.AUDIO_TRACK_KIND) {
                wrapper.consumer.close()
                return@removeAll true
            }
            return@removeAll false
        }
        socket.emit(Protocol.MUTE_HEADSET)
    }

    override suspend fun unmuteHeadset() {
        mutedHeadset = false
        socket.emit(Protocol.UNMUTE_HEADSET) { userAndProducerIds: List<UserAndProducerId> ->
            // 대기실에서는 소비자 생성을 수행하지 않는다.
            if (_device == null) {
                return@emit
            }
            for (userAndProducerId in userAndProducerIds) {
                createReceiveTransportAndConsume(
                    userId = userAndProducerId.userId,
                    remoteProducerId = userAndProducerId.producerId
                )
            }
        }
    }

    override suspend fun startPomodoroTimer() {
        socket.emit(Protocol.START_TIMER)
    }

    override suspend fun sendChat(message: String) {
        logger.d { "Send message: $message" }
        socket.emit(Protocol.SEND_CHAT, message)
    }

    override suspend fun kickUser(userId: String) {
        socket.emit(Protocol.KICK_USER, userId)
    }

    override suspend fun blockUser(userId: String) {
        socket.emit(Protocol.BLOCK_USER, userId)
    }

    override suspend fun unblockUser(userId: String): Result<Unit> =
        suspendCoroutineWithTimeout { continuation ->
            socket.emitWithPrimitiveCallBack(
                Protocol.UNBLOCK_USER,
                userId
            ) { isSuccess: Boolean, message: String ->
                val result = if (isSuccess) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(message))
                }
                continuation.resume(result) {}
            }
        }

    override suspend fun updateAndStopTimer(newProperty: PomodoroTimerProperty) {
        socket.emit(Protocol.EDIT_AND_STOP_TIMER, JSONObject(newProperty.toJson()))
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
        on(Protocol.SEND_CHAT) { message: ChatMessage ->
            logger.d { "Chat message received: $message" }
            eventFlow.tryEmit(StudyRoomEvent.OnReceiveChatMessage(message = message))
        }
        on(Protocol.START_TIMER) {
            eventFlow.tryEmit(StudyRoomEvent.TimerStateChanged(PomodoroTimerState.STARTED))
        }
        on(Protocol.START_SHORT_BREAK) {
            eventFlow.tryEmit(StudyRoomEvent.TimerStateChanged(PomodoroTimerState.SHORT_BREAK))
        }
        on(Protocol.START_LONG_BREAK) {
            eventFlow.tryEmit(StudyRoomEvent.TimerStateChanged(PomodoroTimerState.LONG_BREAK))
        }
        on(Protocol.EDIT_AND_STOP_TIMER) { newProperty: PomodoroTimerProperty ->
            eventFlow.tryEmit(StudyRoomEvent.TimerPropertyChanged(newProperty))
        }
        on(Protocol.OTHER_PEER_DISCONNECTED) { response: OtherPeerDisconnectedResponse ->
            val disposedPeerId = response.disposedPeerId
            receiveTransportWrappers.removeAll { it.userId == disposedPeerId }
            eventFlow.tryEmit(StudyRoomEvent.OnDisconnectPeer(disposedPeerId = disposedPeerId))
        }
        onPrimitiveCallback(Protocol.KICK_USER) { userId: String ->
            eventFlow.tryEmit(StudyRoomEvent.OnKicked(userId = userId))
            if (userId == currentUserId) {
                disconnect()
            }
        }
        onPrimitiveCallback(Protocol.BLOCK_USER) { userId: String ->
            eventFlow.tryEmit(StudyRoomEvent.OnBlocked(userId = userId))
            if (userId == currentUserId) {
                disconnect()
            }
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
            ) { _: String -> }
            // TODO: Producer ID 리턴하기. runBlocking과 suspendCoroutineWithTimeout로 감싸서 호출하려 했으나
            //  emit을 했을 때 서버쪽에서 수신되지 않음.
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
                if (response.kind == MediaStreamTrack.AUDIO_TRACK_KIND && mutedHeadset) {
                    return@emit
                }

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
        logger.d { "Disconnect socket service" }
        _socket?.disconnect()
        _socket = null
        eventFlow.resetReplayCache()
        _device = null

        // close() 호출 후에 dispose()를 호출해야함
        // https://github.com/haiyangwu/mediasoup-client-android/issues/12#issuecomment-792287813
        _sendTransport?.close()
        for (wrapper in receiveTransportWrappers) {
            wrapper.transport.close()
        }

        _sendTransport?.dispose()
        _sendTransport = null
        for (wrapper in receiveTransportWrappers) {
            if (!wrapper.transport.isClosed) {
                wrapper.transport.dispose()
            }
        }
        receiveTransportWrappers.clear()
        mutedHeadset = false
    }

    companion object {

        private val TimeOutMilli = if (BuildConfig.DEBUG) 20_000L else 10_000L

        private suspend inline fun <T> suspendCoroutineWithTimeout(
            crossinline block: (CancellableContinuation<T>) -> Unit
        ): T {
            return withTimeout(TimeOutMilli) {
                suspendCancellableCoroutine(block)
            }
        }
    }
}
