package io.foundy.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.designsystem.util.nonScaledSp
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.UserOverview

@Composable
fun RoomItem(modifier: Modifier = Modifier, room: RoomOverview) {
    Surface(modifier = modifier, color = CamstudyTheme.colorScheme.systemBackground) {
        Row {
            ThumbnailImage(
                imageUrl = room.thumbnail,
                contentDescription = stringResource(R.string.room_thumbnail, room.title)
            )
            Box(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1.0f)) {
                RoomTitle(title = room.title, isPrivate = room.hasPassword)
                Box(modifier = Modifier.height(2.dp))
                Tags(tags = room.tags)
                Box(modifier = Modifier.height(4.dp))
                JoinerImages(
                    joinerImages = room.joinedUsers.map { it.profileImage },
                    maxCount = room.maxCount
                )
            }
        }
    }
}

@Composable
fun ThumbnailImage(imageUrl: String?, contentDescription: String) {
    val thumbnailModifier = Modifier
        .size(64.dp)
        .clip(RoundedCornerShape(12.dp))

    if (imageUrl != null) {
        AsyncImage(
            modifier = thumbnailModifier,
            model = imageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = contentDescription
        )
    } else {
        Surface(
            modifier = thumbnailModifier,
            color = CamstudyTheme.colorScheme.systemUi02
        ) {}
    }
}

private val PrivateIconHorizontalPadding = 4.dp
private val PrivateIconSize = 20.dp
private val PrivateIconInnerPadding = 3.dp

@Composable
private fun RoomTitle(title: String, isPrivate: Boolean) {
    val titleMedium = CamstudyTheme.typography.titleMedium
    val color = CamstudyTheme.colorScheme.systemUi08

    BoxWithConstraints {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CamstudyText(
                modifier = Modifier
                    .height(22.dp)
                    .widthIn(
                        max = this@BoxWithConstraints.maxWidth -
                            PrivateIconHorizontalPadding -
                            PrivateIconSize -
                            PrivateIconInnerPadding
                    ),
                text = title,
                style = titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = titleMedium.fontSize.value.nonScaledSp,
                    color = color
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (isPrivate) {
                Box(modifier = Modifier.padding(horizontal = PrivateIconHorizontalPadding)) {
                    CamstudyIcon(
                        modifier = Modifier
                            .size(PrivateIconSize)
                            .padding(PrivateIconInnerPadding),
                        icon = CamstudyIcons.LockSharp,
                        tint = color,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun Tags(tags: List<String>) {
    val labelMedium = CamstudyTheme.typography.labelMedium

    CamstudyText(
        modifier = Modifier.height(16.dp),
        text = tags.map { "#$it" }.joinToString(" ") { it },
        style = labelMedium.copy(
            color = CamstudyTheme.colorScheme.systemUi05,
            fontSize = labelMedium.fontSize.value.nonScaledSp
        )
    )
}

@Composable
private fun JoinerImages(joinerImages: List<String?>, maxCount: Int) {
    Row {
        val modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))

        repeat(maxCount) { index ->
            val isLast = index != maxCount - 1
            val rightPaddingBox: @Composable () -> Unit = {
                if (isLast) Box(modifier = Modifier.width(4.dp))
            }

            if (joinerImages.size <= index) {
                Row {
                    Surface(
                        modifier = modifier,
                        color = CamstudyTheme.colorScheme.systemUi01
                    ) {}
                    rightPaddingBox()
                }
                return@repeat
            }

            Row {
                Surface(
                    modifier = modifier,
                    color = CamstudyTheme.colorScheme.systemUi01
                ) {
                    AsyncImage(
                        modifier = modifier,
                        model = joinerImages[index],
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(
                            id = io.foundy.core.designsystem.R.drawable.ic_person
                        ),
                        contentDescription = null
                    )
                }
                rightPaddingBox()
            }
        }
    }
}

@Preview(fontScale = 1.0f)
@Composable
private fun RoomItemPreview() {
    CamstudyTheme {
        RoomItem(
            room = RoomOverview(
                id = "id",
                title = "공시족 모여라",
                masterId = "id",
                hasPassword = false,
                thumbnail = null,
                joinCount = 1,
                joinedUsers = listOf(
                    UserOverview(id = "id", name = "김민성", profileImage = null, introduce = null)
                ),
                maxCount = 4,
                tags = listOf("공시", "자격증")
            ),
        )
    }
}

@Preview
@Composable
private fun PrivateRoomItemPreview() {
    CamstudyTheme {
        RoomItem(
            room = RoomOverview(
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
        )
    }
}
