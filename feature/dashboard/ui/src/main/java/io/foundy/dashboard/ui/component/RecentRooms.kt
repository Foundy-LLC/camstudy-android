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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.UserOverview
import io.foundy.core.ui.RoomTags
import io.foundy.core.ui.RoomThumbnailImage
import io.foundy.dashboard.ui.DivideKey
import io.foundy.dashboard.ui.R
import io.foundy.dashboard.ui.RecentRoomsUiState

fun LazyListScope.recentRoomDivide(
    recentRoomsUiState: RecentRoomsUiState,
    onRoomClick: (RoomOverview) -> Unit
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
    when (recentRoomsUiState) {
        RecentRoomsUiState.Loading -> item {
            EmptyDivideContent(text = "")
        }
        is RecentRoomsUiState.Success -> {
            val rooms = recentRoomsUiState.recentRooms
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
        is RecentRoomsUiState.Failure -> item {
            EmptyDivideContent(
                text = recentRoomsUiState.message
                    ?: stringResource(R.string.failed_to_load_recent_room)
            )
        }
    }
}

@Composable
private fun RecentRoomTile(room: RoomOverview, onClick: (RoomOverview) -> Unit) {
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
            RoomThumbnailImage(model = room.thumbnail, contentDescription = null, size = 36.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CamstudyText(
                    text = room.title,
                    style = CamstudyTheme.typography.titleSmall.copy(
                        color = CamstudyTheme.colorScheme.systemUi08,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                recentRoomsUiState = RecentRoomsUiState.Success(
                    recentRooms = listOf(
                        RoomOverview(
                            id = "id",
                            title = "스터디",
                            masterId = "id",
                            hasPassword = true,
                            thumbnail = null,
                            joinCount = 1,
                            joinedUsers = listOf(
                                UserOverview(
                                    id = "id",
                                    name = "김민성",
                                    profileImage = null,
                                    introduce = null
                                )
                            ),
                            maxCount = 4,
                            tags = listOf("공시", "자격증")
                        ),
                        RoomOverview(
                            id = "id2",
                            title = "스터디",
                            masterId = "id",
                            hasPassword = true,
                            thumbnail = null,
                            joinCount = 1,
                            joinedUsers = listOf(
                                UserOverview(
                                    id = "id",
                                    name = "김민성",
                                    profileImage = null,
                                    introduce = null
                                )
                            ),
                            maxCount = 4,
                            tags = listOf("공시", "자격증")
                        )
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
                recentRoomsUiState = RecentRoomsUiState.Success(recentRooms = emptyList()),
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
            RoomOverview(
                id = "id",
                title = "스터디",
                masterId = "id",
                hasPassword = true,
                thumbnail = null,
                joinCount = 1,
                joinedUsers = listOf(
                    UserOverview(id = "id", name = "김민성", profileImage = null, introduce = null)
                ),
                maxCount = 4,
                tags = listOf("공시", "자격증")
            ),
            onClick = {}
        )
    }
}
