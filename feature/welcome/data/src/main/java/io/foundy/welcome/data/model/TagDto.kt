package io.foundy.welcome.data.model

import io.foundy.core.model.Tag

data class TagDto(
    val name: String
)

fun TagDto.toEntity() = Tag(
    name = name
)
