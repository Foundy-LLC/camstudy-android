package io.foundy.organization.data.model

import io.foundy.core.model.OrganizationOverview

data class OrganizationOverviewDto(
    val id: String,
    val name: String
)

fun OrganizationOverviewDto.toEntity() = OrganizationOverview(
    id = id,
    name = name
)
