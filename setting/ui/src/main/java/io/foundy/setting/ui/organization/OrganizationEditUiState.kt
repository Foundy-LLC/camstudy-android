package io.foundy.setting.ui.organization

import io.foundy.core.model.Organization
import io.foundy.core.model.OrganizationOverview
import io.foundy.core.ui.UserMessage

sealed class OrganizationEditUiState {

    object Loading : OrganizationEditUiState()

    data class Success(
        val registeredOrganizations: List<OrganizationOverview>,
        val selectedOrganization: Organization? = null,
        val recommendedOrganizations: List<Organization> = emptyList(),
        val name: String = "",
        val email: String = "",
        val onNameChange: (String) -> Unit,
        val onEmailChange: (String) -> Unit,
        val onDeleteClick: (OrganizationOverview) -> Unit,
        val onRequestEmailClick: () -> Unit
    ) : OrganizationEditUiState()

    data class Failure(val userMessage: UserMessage) : OrganizationEditUiState()
}
