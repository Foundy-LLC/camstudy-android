package io.foundy.setting.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.User
import io.foundy.core.ui.UserProfileInfoGroup
import io.foundy.core.ui.clearImageCache
import io.foundy.setting.ui.component.UserTile
import io.foundy.setting.ui.component.UserTileShimmer
import io.foundy.setting.ui.destinations.EditProfileRouteDestination
import io.foundy.setting.ui.model.EditProfileResult
import io.foundy.setting.ui.profile.StringList
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun SettingRoute(
    navigator: DestinationsNavigator,
    profileEditResultRecipient: OpenResultRecipient<EditProfileResult>,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    profileEditResultRecipient.onNavResult {
        when (it) {
            is NavResult.Value -> {
                viewModel.updateProfile(it.value)
                val profileImage = it.value.profileImage
                if (profileImage != null) {
                    context.clearImageCache(profileImage)
                }
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.profile_updated))
                }
            }
            NavResult.Canceled -> {}
        }
    }

    SettingScreen(
        uiState = uiState,
        popBackStack = navigator::popBackStack,
        snackbarHostState = snackbarHostState,
        onUserTileClick = { user ->
            navigator.navigate(
                EditProfileRouteDestination(
                    name = user.name,
                    introduce = user.introduce,
                    imageUrl = user.profileImage,
                    tags = StringList(user.tags)
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    uiState: SettingUiState,
    snackbarHostState: SnackbarHostState,
    popBackStack: () -> Unit,
    onUserTileClick: (User) -> Unit
) {
    Scaffold(
        containerColor = CamstudyTheme.colorScheme.systemUi01,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                is SettingUiState.Success -> SuccessContent(
                    uiState = uiState,
                    onUserTileClick = onUserTileClick
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(uiState: SettingUiState.Success, onUserTileClick: (User) -> Unit) {
    val user = uiState.currentUser

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            UserTile(
                name = user.name,
                profileImageUrl = user.profileImage,
                organization = user.organizations.firstOrNull(),
                introduce = user.introduce,
                tags = user.tags,
                onClick = { onUserTileClick(user) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            UserProfileInfoGroup(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                weeklyRankingOverall = user.weeklyRankingOverall,
                weeklyStudyTimeSec = user.weeklyStudyTimeSec,
                weeklyRanking = user.weeklyRanking,
                consecutiveStudyDays = user.consecutiveStudyDays,
                growingCrop = user.growingCrop,
                harvestedCrops = user.harvestedCrops
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
