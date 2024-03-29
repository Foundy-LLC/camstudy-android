package io.foundy.feature.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.constant.RoomConstants
import io.foundy.core.ui.pullrefresh.RefreshableContent
import io.foundy.feature.dashboard.ui.component.Header
import io.foundy.feature.dashboard.ui.component.recentRoomDivide
import io.foundy.feature.dashboard.ui.component.recommendedRoomDivide
import io.foundy.feature.room.ui.RoomActivity
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

internal enum class DivideKey {
    RecentRooms,
    RecommendedRooms;
}

@Composable
fun DashboardRoute(
    viewModel: DashboardViewModel = hiltViewModel(),
    plantResultRecipient: OpenResultRecipient<Boolean>,
    navigateToCropTab: () -> Unit,
    navigateToPlantCrop: () -> Unit,
    navigateToRankingTab: () -> Unit,
    showSnackbar: (id: String) -> Unit
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current

    plantResultRecipient.onNavResult {
        when (it) {
            is NavResult.Value -> {
                uiState.fetchGrowingCrop()
                showSnackbar(context.getString(R.string.success_to_plant_crop))
            }
            else -> {}
        }
    }

    viewModel.collectSideEffect {
        when (it) {
            else -> {
                /* TODO */
            }
        }
    }

    DashboardScreen(
        uiState = uiState,
        onCropTileClick = { growingCrop ->
            if (growingCrop != null) {
                navigateToCropTab()
            } else {
                navigateToPlantCrop()
            }
        },
        joinRoom = { roomOverview ->
            val intent = RoomActivity.getIntent(context, roomOverview = roomOverview)
            context.startActivity(intent)
        },
        onSeeRankingClick = navigateToRankingTab
    )
}

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onCropTileClick: (GrowingCrop?) -> Unit,
    joinRoom: (RoomOverview) -> Unit,
    onSeeRankingClick: () -> Unit
) {
    RefreshableContent(
        modifier = Modifier
            .background(color = CamstudyTheme.colorScheme.systemUi01)
            .fillMaxHeight(),
        refreshing = uiState.isLoading,
        onRefresh = uiState.onRefresh
    ) {
        LazyColumn {
            item {
                Header(
                    userRankingUiState = uiState.userRankingUiState,
                    growingCropUiState = uiState.growingCropUiState,
                    onCropTileClick = onCropTileClick,
                    onSeeRankingClick = onSeeRankingClick
                )
            }
            dividePadding()
            recentRoomDivide(
                recentRoomsUiState = uiState.recentRoomsUiState,
                onRoomClick = joinRoom
            )
            dividePadding()
            recommendedRoomDivide(
                uiState = uiState.recommendedRoomsUiState,
                onJoinClick = joinRoom
            )
        }
    }
}

private fun LazyListScope.dividePadding() {
    item {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(widthDp = 360)
@Composable
private fun DashboardScreenPreview() {
    val recommendedRooms = listOf(
        RoomOverview(
            id = "id1",
            title = "방제목",
            masterId = "id",
            hasPassword = true,
            thumbnail = null,
            joinCount = 0,
            joinedUsers = emptyList(),
            maxCount = RoomConstants.MaxPeerCount,
            tags = listOf("tag1")
        ),
        RoomOverview(
            id = "id2",
            title = "방제목2",
            masterId = "id",
            hasPassword = true,
            thumbnail = null,
            joinCount = 0,
            joinedUsers = emptyList(),
            maxCount = RoomConstants.MaxPeerCount,
            tags = listOf("tag1")
        ),
    )

    CamstudyTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                fetchGrowingCrop = {},
                recommendedRoomsUiState = RecommendedRoomsUiState.Success(
                    rooms = recommendedRooms
                ),
                onRefresh = {}
            ),
            onCropTileClick = {},
            joinRoom = {},
            onSeeRankingClick = {}
        )
    }
}
