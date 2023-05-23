package io.foundy.setting.ui.organization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import io.foundy.core.model.Organization
import io.foundy.core.model.OrganizationOverview
import io.foundy.core.ui.UserMessage
import io.foundy.setting.ui.R

sealed class OrganizationEditUiState {

    object Loading : OrganizationEditUiState()

    data class Success(
        val registeredOrganizations: List<OrganizationOverview>,
        val recommendedOrganizations: List<Organization> = emptyList(),
        val name: String = "",
        val email: String = "",
        val deletingOrganizationIds: Set<String> = emptySet(),
        val onNameChange: (String) -> Unit,
        val onEmailChange: (String) -> Unit,
        val onDeleteClick: (OrganizationOverview) -> Unit,
        val onRequestEmailClick: () -> Unit
    ) : OrganizationEditUiState() {

        val selectedOrganization: Organization?
            get() {
                return recommendedOrganizations.firstOrNull { it.name == name }
            }
        
        val shouldShowNameError: Boolean
            get() {
                return name.isNotEmpty() && selectedOrganization == null
            }

        val nameSupportingText: String
            @Composable
            @ReadOnlyComposable
            get() {
                if (selectedOrganization != null) {
                    return stringResource(R.string.organization_name_is_valid_supporting_text)
                }
                if (name.isNotEmpty()) {
                    return stringResource(R.string.organization_name_error_supporting_text)
                }
                return stringResource(id = R.string.organization_name_supporting_text)
            }
    }

    data class Failure(val userMessage: UserMessage) : OrganizationEditUiState()
}
