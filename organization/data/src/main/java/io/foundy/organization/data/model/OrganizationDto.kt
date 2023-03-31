package io.foundy.organization.data.model

import io.foundy.core.model.Organization

data class OrganizationDto(
    val id: String,
    val name: String,
    val address: String
)

fun OrganizationDto.toEntity() = Organization(
    id = id,
    name = name,
    address = address
)
