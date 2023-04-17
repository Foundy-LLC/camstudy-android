package io.foundy.dashboard.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.ui.RoomTags
import io.foundy.core.ui.RoomThumbnailImage
import io.foundy.dashboard.ui.DivideKey
import io.foundy.dashboard.ui.R
import io.foundy.dashboard.ui.model.RecentRoom

fun LazyListScope.recentRoomDivide(
    rooms: List<RecentRoom>,
    onRoomClick: (RecentRoom) -> Unit
) {
    item {
        DivideTitle(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = CamstudyTheme.colorScheme.systemBackground)
                .padding(16.dp),
            text = stringResource(R.string.recent_study_room)
        )
    }
    if (rooms.isEmpty()) {
        item {
            EmptyDivideContent(text = stringResource(R.string.no_recent_rooms))
        }
    } else {
        items(items = rooms, key = { "${DivideKey.RecentRooms}${it.id}" }) { room ->
            RecentRoomTile(room = room, onClick = onRoomClick)
        }
    }
}

@Composable
private fun RecentRoomTile(room: RecentRoom, onClick: (RecentRoom) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(room) }
            .background(color = CamstudyTheme.colorScheme.systemBackground)
    ) {
        CamstudyDivider()
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoomThumbnailImage(imageUrl = room.thumbnail, contentDescription = null, size = 36.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CamstudyText(
                    text = room.title,
                    style = CamstudyTheme.typography.titleSmall.copy(
                        color = CamstudyTheme.colorScheme.systemUi08,
                        fontWeight = FontWeight.Bold
                    )
                )
                RoomTags(tags = room.tags)
            }
            CamstudyIcon(
                modifier = Modifier
                    .width(10.dp)
                    .height(18.dp),
                icon = CamstudyIcons.ArrowForward,
                tint = CamstudyTheme.colorScheme.systemUi06,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun RecentRoomsPreview() {
    CamstudyTheme {
        LazyColumn {
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
                onRoomClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun EmptyRecentRoomsPreview() {
    CamstudyTheme {
        LazyColumn {
            recentRoomDivide(
                rooms = emptyList(),
                onRoomClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun RecentRoomTilePreview() {
    CamstudyTheme {
        RecentRoomTile(
            RecentRoom(
                id = "id",
                thumbnail = null,
                title = "방제목",
                tags = listOf("안드로이드", "개발")
            ),
            onClick = {}
        )
    }
}
