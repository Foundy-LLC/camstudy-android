package io.foundy.data.model.common

data class ResponseBody<out T>(
    val message: String,
    val data: T
)
