package io.foundy.organization.ui

import androidx.paging.PagingData
import io.foundy.core.model.Organization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class OrganizationUiState(
    val organizationsFlow: Flow<PagingData<Organization>> = emptyFlow(),
    val query: String = "",
    val onQueryChange: (String) -> Unit
)
