package io.foundy.setting.ui.organization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import io.foundy.core.model.Organization
import io.foundy.core.model.OrganizationOverview
import io.foundy.core.ui.UserMessage
import io.foundy.setting.ui.R

sealed class OrganizationEditUiState {

    object Loading : OrganizationEditUiState()

    data class Success(
        val registeredOrganizations: List<OrganizationOverview>,
        private val recommendedOrganizations: List<Organization> = emptyList(),
        val name: String = "",
        val email: String = "",
        val deletingOrganizationIds: Set<String> = emptySet(),
        val inRequesting: Boolean = false,
        val onNameChange: (String) -> Unit,
        val onEmailChange: (String) -> Unit,
        val onDeleteClick: (OrganizationOverview) -> Unit,
        val onRequestEmailClick: () -> Unit
    ) : OrganizationEditUiState() {

        val recommendedOrganizationNames: List<String> = run {
            recommendedOrganizations
                .filterNot { it.name == name }
                .map { it.name }
                .take(3)
        }

        val selectedOrganization: Organization? = run {
            recommendedOrganizations.firstOrNull { it.name == name }
        }

        private val isOrganizationAlreadyRegistered = run {
            registeredOrganizations.any { it.name == name }
        }

        private val isValidEmailForSelectedOrganization: Boolean = run {
            val selectedOrganizationEmailAddress = selectedOrganization?.address
            if (selectedOrganizationEmailAddress != null) {
                val enteredEmailAddress = email
                    .split("@")
                    .getOrNull(1) ?: return@run false
                return@run enteredEmailAddress == selectedOrganizationEmailAddress
            }
            return@run false
        }

        private val isValidEmailFormat: Boolean = run {
            EMAIL_ADDRESS.matcher(email).matches()
        }

        val shouldShowNameError: Boolean
            get() {
                if (isOrganizationAlreadyRegistered) {
                    return true
                }
                if (selectedOrganization == null) {
                    return false
                }
                return name.isNotEmpty()
            }

        val shouldShowEmailError: Boolean
            get() {
                if (email.isEmpty()) {
                    return false
                }
                if (selectedOrganization != null) {
                    return !isValidEmailForSelectedOrganization
                }
                return !isValidEmailFormat
            }

        val nameSupportingText: String
            @Composable
            @ReadOnlyComposable
            get() {
                if (isOrganizationAlreadyRegistered) {
                    return stringResource(R.string.already_registered_organization)
                }
                if (selectedOrganization != null) {
                    return stringResource(R.string.organization_name_is_valid_supporting_text)
                }
                if (name.isNotEmpty()) {
                    return stringResource(R.string.organization_name_error_supporting_text)
                }
                return stringResource(id = R.string.organization_name_supporting_text)
            }

        val emailSupportingText: String
            @Composable
            @ReadOnlyComposable
            get() {
                if (selectedOrganization != null) {
                    return if (isValidEmailForSelectedOrganization) {
                        stringResource(R.string.valid_email_supporting_text)
                    } else {
                        stringResource(
                            R.string.invalid_organization_supporting_text,
                            selectedOrganization.name,
                            selectedOrganization.address
                        )
                    }
                }
                if (email.isNotEmpty() && !isValidEmailFormat) {
                    return stringResource(R.string.invalid_email_format_supporting_text)
                }
                return stringResource(R.string.organization_email_supporting_text)
            }

        val canRequest: Boolean = run {
            !isOrganizationAlreadyRegistered &&
                selectedOrganization != null &&
                isValidEmailForSelectedOrganization &&
                !inRequesting
        }
    }

    data class Failure(val userMessage: UserMessage) : OrganizationEditUiState()
}
