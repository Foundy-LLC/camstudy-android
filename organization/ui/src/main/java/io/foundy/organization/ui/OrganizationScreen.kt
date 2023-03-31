package io.foundy.organization.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.designsystem.component.CamstudyTextField
import org.orbitmvi.orbit.compose.collectAsState

@Composable
@Destination
fun OrganizationRoute(
    viewModel: OrganizationViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState()

    OrganizationScreen(uiState = uiState.value)
}

@Composable
fun OrganizationScreen(uiState: OrganizationUiState) {
    val organizations = uiState.organizationsFlow.collectAsLazyPagingItems()

    LazyColumn {
        item {
            CamstudyTextField(
                value = uiState.query,
                onValueChange = uiState.onQueryChange
            )
        }
        items(organizations, key = { it.id }) { organization ->
            if (organization == null) {
                return@items
            }
            Text(modifier = Modifier.padding(8.dp), text = organization.name)
        }
        // TODO: 로딩, 에러 보이기
    }
}
