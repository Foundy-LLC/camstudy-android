package io.foundy.setting.ui

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
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item {
                CamstudyText(text = uiState.currentUser.toString())
            }
        }
    }
}
