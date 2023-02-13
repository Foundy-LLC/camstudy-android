package io.foundy.core.data.model

data class ResponseBody<out T>(
    val message: String,
    val data: T
)
