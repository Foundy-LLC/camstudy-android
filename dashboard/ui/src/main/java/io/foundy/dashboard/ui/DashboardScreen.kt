package io.foundy.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.constant.RoomConstants
import io.foundy.dashboard.ui.component.Header
import io.foundy.dashboard.ui.component.recentRoomDivide
import io.foundy.dashboard.ui.component.recommendedRoomDivide
import io.foundy.dashboard.ui.model.RecentRoom
import kotlinx.coroutines.flow.flowOf

internal enum class DivideKey {
    RecentRooms,
    RecommendedRooms;
}

@Composable
fun DashboardRoute() {
    val recommendedRoomFlow = flowOf(PagingData.from(emptyList<RoomOverview>()))
    val recommendedRooms = recommendedRoomFlow.collectAsLazyPagingItems()

    DashboardScreen(recommendedRooms = recommendedRooms)
}

// TODO: 실제 데이터 전달하기
@Composable
fun DashboardScreen(
    recommendedRooms: LazyPagingItems<RoomOverview>
) {
    LazyColumn(
        modifier = Modifier
            .background(color = CamstudyTheme.colorScheme.systemUi01)
            .fillMaxHeight()
    ) {
        item {
            Header(
                weeklyStudyMinutes = 2213,
                weeklyRanking = 3,
                growingCrop = null,
                onCropTileClick = { /* TODO: 구현하기 */ }
            )
        }
        dividePadding()
        recentRoomDivide(
            rooms = listOf(
                RecentRoom(
                    id = "id",
                    thumbnail = null,
                    title = "방제목1",
                    tags = listOf("안드로이드", "개발")
                ),
                RecentRoom(
                    id = "id2",
                    thumbnail = null,
                    title = "방제목2",
                    tags = listOf("공부원", "공시")
                )
            ),
            onRoomClick = { /* TODO: 구현하기 */ }
        )
        dividePadding()
        recommendedRoomDivide(
            rooms = recommendedRooms,
            onJoinClick = { /* TODO: 구현하기 */ }
        )
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
    val recommendedRoomFlow = flowOf(
        PagingData.from(
            listOf(
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
        )
    )
    val recommendedRooms = recommendedRoomFlow.collectAsLazyPagingItems()

    CamstudyTheme {
        DashboardScreen(
            recommendedRooms = recommendedRooms
        )
    }
}
