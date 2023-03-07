package com.example.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

@kotlinx.serialization.Serializable
data class ChatMessage(
    val id: String,
    val authorId: String,
    val authorName: String,
    val content: String,
    /** ISO time String format */
    private val sentAt: String,
) {
    val sentDateTime: LocalDateTime
        get() {
            val instant = Instant.parse(sentAt)
            return instant.toLocalDateTime(WebRtcServerTimeZone)
        }
}
