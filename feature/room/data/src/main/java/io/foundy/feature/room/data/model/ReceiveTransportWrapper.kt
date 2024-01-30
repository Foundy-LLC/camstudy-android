package io.foundy.feature.room.data.model

import org.mediasoup.droid.Consumer
import org.mediasoup.droid.RecvTransport

data class ReceiveTransportWrapper(
    val transport: RecvTransport,
    val serverReceiveTransportId: String,
    val producerId: String,
    val userId: String,
    val consumer: Consumer
)
