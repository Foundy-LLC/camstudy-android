package io.foundy.room.data.extension

import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.Socket

inline fun <reified T> Socket.emit(event: String, arg: Any, crossinline callback: (T) -> Unit) {
    val ack = Ack { args ->
        val paredObject = Gson().fromJson(args[0].toString(), T::class.java)
        callback(paredObject)
    }
    this.emit(event, arg, ack)
}
