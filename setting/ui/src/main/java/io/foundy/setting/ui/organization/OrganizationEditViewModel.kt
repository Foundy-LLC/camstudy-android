package io.foundy.setting.ui.organization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.model.OrganizationOverview
import io.foundy.core.ui.UserMessage
import io.foundy.organization.data.repository.OrganizationRepository
import io.foundy.setting.ui.R
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class OrganizationEditViewModel @Inject constructor(
    private val organizationRepository: OrganizationRepository,
    private val authRepository: AuthRepository
) : ViewModel(), ContainerHost<OrganizationEditUiState, OrganizationEditSideEffect> {

    override val container: Container<OrganizationEditUiState, OrganizationEditSideEffect> =
        container(OrganizationEditUiState.Loading)

    private var _currentUserId: String? = null
    private val currentUserId: String get() = requireNotNull(_currentUserId)

    init {
        viewModelScope.launch {
            _currentUserId = requireNotNull(authRepository.currentUserIdStream.firstOrNull())
            organizationRepository.getUserOrganizations(userId = currentUserId)
                .onSuccess { organizations ->
                    intent {
                        reduce {
                            OrganizationEditUiState.Success(
                                registeredOrganizations = organizations,
                                onNameChange = { /* TODO */ },
                                onEmailChange = { /* TODO */ },
                                onDeleteClick = ::deleteOrganization,
                                onRequestEmailClick = { /* TODO */ }
                            )
                        }
                    }
                }.onFailure {
                    intent {
                        reduce {
                            OrganizationEditUiState.Failure(
                                userMessage = UserMessage(
                                    content = it.message,
                                    defaultRes = R.string.failed_to_load_my_organizations
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun addDeletingOrganizationId(id: String) = intent {
        val uiState = state
        check(uiState is OrganizationEditUiState.Success)
        reduce {
            uiState.copy(
                deletingOrganizationIds = uiState.deletingOrganizationIds + id
            )
        }
    }

    private fun removeDeletingOrganizationId(id: String) = intent {
        val uiState = state
        check(uiState is OrganizationEditUiState.Success)
        reduce {
            uiState.copy(
                deletingOrganizationIds = uiState.deletingOrganizationIds - id
            )
        }
    }

    private fun deleteOrganization(organization: OrganizationOverview) = intent {
        addDeletingOrganizationId(organization.id)
        organizationRepository.removeOrganization(
            userId = currentUserId,
            organizationId = organization.id
        ).onSuccess {
            (state as? OrganizationEditUiState.Success)?.let { uiState ->
                reduce {
                    uiState.copy(
                        registeredOrganizations = uiState.registeredOrganizations - organization
                    )
                }
            }
            postSideEffect(
                OrganizationEditSideEffect.Message(
                    userMessage = UserMessage(defaultRes = R.string.removed_organization)
                )
            )
        }.onFailure {
            postSideEffect(
                OrganizationEditSideEffect.Message(
                    userMessage = UserMessage(
                        content = it.message,
                        defaultRes = R.string.failed_to_remove_organization
                    )
                )
            )
        }
        removeDeletingOrganizationId(organization.id)
    }
}
