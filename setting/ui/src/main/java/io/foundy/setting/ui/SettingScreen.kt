package io.foundy.setting.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.setting.ui.component.UserTile
import io.foundy.setting.ui.component.UserTileShimmer
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun SettingRoute(
    navigator: DestinationsNavigator,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value

    SettingScreen(uiState = uiState, popBackStack = navigator::popBackStack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    uiState: SettingUiState,
    popBackStack: () -> Unit
) {
    Scaffold(
        topBar = {
            CamstudyTopAppBar(
                onBackClick = popBackStack,
                title = { CamstudyText(text = stringResource(R.string.my_profile)) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (uiState) {
                is SettingUiState.Failure -> Box { /* TODO */ }
                SettingUiState.Loading -> LoadingContent()
                is SettingUiState.Success -> SuccessContent(uiState = uiState)
            }
        }
    }
}

@Composable
private fun SuccessContent(uiState: SettingUiState.Success) {
    val user = uiState.currentUser

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            UserTile(
                name = user.name,
                profileImageUrl = user.profileImage,
                organization = user.organizations.firstOrNull(),
                introduce = user.introduce,
                tags = user.tags,
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        item {
            UserTileShimmer()
        }
    }
}
