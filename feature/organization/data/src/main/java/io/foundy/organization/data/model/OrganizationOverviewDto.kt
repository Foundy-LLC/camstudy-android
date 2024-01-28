package io.foundy.organization.data.model

import com.google.gson.annotations.SerializedName
import io.foundy.core.model.OrganizationOverview

data class OrganizationOverviewDto(
    @SerializedName("organizationId") val id: String,
    @SerializedName("organizationName") val name: String
)

fun OrganizationOverviewDto.toEntity() = OrganizationOverview(
    id = id,
    name = name
)
