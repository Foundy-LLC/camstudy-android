package io.foundy.organization.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.organization.data.repository.OrganizationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class OrganizationViewModel @Inject constructor(
    private val repository: OrganizationRepository
) : ViewModel(), ContainerHost<OrganizationUiState, OrganizationSideEffect> {

    override val container: Container<OrganizationUiState, OrganizationSideEffect> = container(
        OrganizationUiState(
            onQueryChange = ::updateQuery
        )
    )

    private var organizationQueryJob: Job? = null

    init {
        queryOrganizations("")
    }

    private fun updateQuery(query: String) = intent {
        reduce { state.copy(query = query) }
        organizationQueryJob?.cancel()
        organizationQueryJob = viewModelScope.launch {
            delay(500)
            queryOrganizations(query)
        }
    }

    private fun queryOrganizations(query: String) = intent {
        val organizationsFlow = repository.getOrganizations(name = query).cachedIn(viewModelScope)
        reduce {
            state.copy(organizationsFlow = organizationsFlow)
        }
    }
}
