package io.foundy.feature.dashboard.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.constant.RoomConstants
import io.foundy.core.ui.RoomTileWithJoinButton
import io.foundy.feature.dashboard.ui.DivideKey
import io.foundy.feature.dashboard.ui.R
import io.foundy.feature.dashboard.ui.RecommendedRoomsUiState

fun LazyListScope.recommendedRoomDivide(
    uiState: RecommendedRoomsUiState,
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
    when (uiState) {
        is RecommendedRoomsUiState.Failure -> failure()
        RecommendedRoomsUiState.Loading -> loading()
        is RecommendedRoomsUiState.Success -> success(
            uiState = uiState,
            onJoinClick = onJoinClick
        )
    }
}

private fun LazyListScope.failure() {
    item {
        // TODO
    }
}

private fun LazyListScope.loading() {
    item {
        // TODO
    }
}

private fun LazyListScope.success(
    uiState: RecommendedRoomsUiState.Success,
    onJoinClick: (RoomOverview) -> Unit
) {
    val rooms = uiState.rooms

    if (rooms.isEmpty()) {
        item {
            EmptyDivideContent(text = stringResource(R.string.no_recommended_rooms))
        }
    } else {
        items(items = rooms, key = { "${DivideKey.RecommendedRooms}${it.id}" }) { room ->
            Box {
                RoomTileWithJoinButton(
                    modifier = Modifier.fillMaxWidth(),
                    room = room,
                    onJoinClick = onJoinClick
                )
                CamstudyDivider()
            }
        }
    }
}

@Preview
@Composable
private fun RecommendedRoomDividePreview() {
    val rooms = listOf(
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

    CamstudyTheme {
        LazyColumn {
            recommendedRoomDivide(
                uiState = RecommendedRoomsUiState.Success(rooms = rooms),
                onJoinClick = {}
            )
        }
    }
}
