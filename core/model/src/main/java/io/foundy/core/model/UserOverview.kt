package io.foundy.core.model

data class UserOverview(
    val id: String,
    val name: String,
    val profileImage: String?,
    val introduce: String?
) : java.io.Serializable
