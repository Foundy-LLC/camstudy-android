package io.foundy.user.data.model

data class UserUpdateRequestBody(
    val userId: String,
    val nickName: String,
    val introduce: String?,
    val tags: List<String>
)
