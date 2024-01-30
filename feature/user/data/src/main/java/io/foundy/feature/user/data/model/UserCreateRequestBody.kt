package io.foundy.feature.user.data.model

data class UserCreateRequestBody(
    val userId: String,
    val name: String,
    val introduce: String?,
    val tags: List<String>
)
