package io.foundy.room.data.extension

import io.socket.client.Ack
import io.socket.client.Socket
import io.socket.emitter.Emitter.Listener
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

inline fun <reified T> Socket.emit(event: String, crossinline callback: (T) -> Unit) {
    val ack = Ack { args ->
        val paredObject = Json.decodeFromString<T>(args[0].toString())
        callback(paredObject)
    }
    this.emit(event, ack)
}

inline fun <reified T> Socket.emit(event: String, arg: Any, crossinline callback: (T) -> Unit) {
    val ack = Ack { args ->
        val paredObject = Json.decodeFromString<T>(args[0].toString())
        callback(paredObject)
    }
    this.emit(event, arg, ack)
}

inline fun <reified T> Socket.emitWithPrimitiveCallBack(
    event: String,
    arg: Any,
    crossinline callback: (T) -> Unit
) {
    val ack = Ack { args ->
        callback(args[0] as T)
    }
    this.emit(event, arg, ack)
}

inline fun <reified S, reified F> Socket.emit(
    event: String,
    arg: Any,
    crossinline onSuccess: (S) -> Unit,
    crossinline onFailure: (F) -> Unit
) {
    val ack = Ack { args ->
        try {
            val paredSuccessObject = Json.decodeFromString<S>(args[0].toString())
            onSuccess(paredSuccessObject)
        } catch (e: IllegalArgumentException) {
            val paredFailureObject = Json.decodeFromString<F>(args[0].toString())
            onFailure(paredFailureObject)
        }
    }
    this.emit(event, arg, ack)
}

inline fun <reified T> Socket.on(event: String, crossinline callback: (T) -> Unit) {
    val listener = Listener { args ->
        val paredObject = Json.decodeFromString<T>(args[0].toString())
        callback(paredObject)
    }
    this.on(event, listener)
}

inline fun <reified T> Socket.onPrimitiveCallback(
    event: String,
    crossinline callback: (T) -> Unit
) {
    val listener = Listener { args ->
        val paredObject = args[0] as T
        callback(paredObject)
    }
    this.on(event, listener)
}
