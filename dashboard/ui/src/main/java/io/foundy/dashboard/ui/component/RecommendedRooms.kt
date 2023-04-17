package io.foundy.dashboard.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.constant.RoomConstants
import io.foundy.core.ui.RoomTileWithJoinButton
import io.foundy.dashboard.ui.DivideKey
import io.foundy.dashboard.ui.R
import kotlinx.coroutines.flow.flowOf

fun LazyListScope.recommendedRoomDivide(
    rooms: LazyPagingItems<RoomOverview>,
    onJoinClick: (RoomOverview) -> Unit
) {
    item {
        DivideTitle(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = CamstudyTheme.colorScheme.systemBackground)
                .padding(16.dp),
            text = stringResource(R.string.recommended_rooms)
        )
    }
    if (rooms.itemCount == 0) {
        item {
            EmptyDivideContent(text = stringResource(R.string.no_recommended_rooms))
        }
    } else {
        items(items = rooms, key = { "${DivideKey.RecommendedRooms}${it.id}" }) { room ->
            if (room == null) {
                // TODO: Doing something
                return@items
            }
            Box {
                RoomTileWithJoinButton(
                    modifier = Modifier.fillMaxWidth(),
                    room = room,
                    onJoinClick = onJoinClick
                )
                CamstudyDivider()
            }
            // TODO: Maybe show the loadindg progress bar
        }
    }
}

@Preview
@Composable
private fun RecommendedRoomDividePreview() {
    val roomFlow = flowOf(
        PagingData.from(
            listOf(
                RoomOverview(
                    id = "id",
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
    val rooms = roomFlow.collectAsLazyPagingItems()

    CamstudyTheme {
        LazyColumn {
            recommendedRoomDivide(
                rooms = rooms,
                onJoinClick = {}
            )
        }
    }
}
