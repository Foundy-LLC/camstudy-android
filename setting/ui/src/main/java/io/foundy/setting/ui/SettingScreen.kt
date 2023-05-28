package io.foundy.setting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.User
import io.foundy.core.ui.UserProfileInfoGroup
import io.foundy.core.ui.clearImageCache
import io.foundy.setting.ui.component.UserTile
import io.foundy.setting.ui.component.UserTileShimmer
import io.foundy.setting.ui.destinations.EditProfileRouteDestination
import io.foundy.setting.ui.destinations.OrganizationEditRouteDestination
import io.foundy.setting.ui.model.EditProfileResult
import io.foundy.setting.ui.profile.StringList
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

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

    viewModel.collectSideEffect {
        when (it) {
            is SettingSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it.content ?: context.getString(it.defaultRes)
                )
            }
        }
    }

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
        },
        onOrganizationEditClick = {
            navigator.navigate(OrganizationEditRouteDestination)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    uiState: SettingUiState,
    snackbarHostState: SnackbarHostState,
    popBackStack: () -> Unit,
    onUserTileClick: (User) -> Unit,
    onOrganizationEditClick: () -> Unit
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
                    onUserTileClick = onUserTileClick,
                    onOrganizationEditClick = onOrganizationEditClick
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: SettingUiState.Success,
    onUserTileClick: (User) -> Unit,
    onOrganizationEditClick: () -> Unit
) {
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
            UserProfileInfoGroup(
                modifier = Modifier
                    .background(color = CamstudyTheme.colorScheme.systemBackground)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                weeklyRankingOverall = user.weeklyRankingOverall,
                weeklyStudyTimeSec = user.weeklyStudyTimeSec,
                weeklyRanking = user.weeklyRanking,
                consecutiveStudyDays = user.consecutiveStudyDays,
                growingCrop = user.growingCrop,
                harvestedCrops = user.harvestedCrops,
                hasWeeklyRankingScore = user.hasWeeklyRanking
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            TileItem(
                leadingIcon = CamstudyIcons.BusinessCenter,
                title = stringResource(R.string.organization_edit),
                onClick = onOrganizationEditClick
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

@Composable
fun TileItem(
    leadingIcon: CamstudyIcon,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CamstudyIcon(
            modifier = Modifier.size(24.dp),
            icon = leadingIcon,
            contentDescription = null,
            tint = CamstudyTheme.colorScheme.systemUi08
        )
        Spacer(modifier = Modifier.width(12.dp))
        CamstudyText(
            modifier = Modifier.weight(1f),
            text = title,
            style = CamstudyTheme.typography.titleMedium.copy(
                color = CamstudyTheme.colorScheme.systemUi08,
                fontWeight = FontWeight.SemiBold
            )
        )
        CamstudyIcon(
            modifier = Modifier.size(width = 13.dp, height = 16.dp),
            icon = CamstudyIcons.ArrowForward,
            contentDescription = null,
            tint = CamstudyTheme.colorScheme.systemUi07
        )
    }
}

@Preview
@Composable
fun TileItemPreview() {
    CamstudyTheme {
        TileItem(leadingIcon = CamstudyIcons.BusinessCenter, title = "소속 편집", onClick = {})
    }
}
