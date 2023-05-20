package io.foundy.setting.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.ui.UserProfileImage

private val ProfileShape = RoundedCornerShape(8.dp)
private val ProfileSize = 64.dp

@Composable
fun UserTile(
    name: String,
    profileImageUrl: String?,
    organization: String?,
    introduce: String?,
    tags: List<String>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserProfileImage(
            imageUrl = profileImageUrl,
            imageOrContainerSize = ProfileSize,
            fallbackIconSize = 40.dp,
            cornerShape = ProfileShape
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CamstudyText(
                    text = name,
                    style = CamstudyTheme.typography.titleMedium.copy(
                        color = CamstudyTheme.colorScheme.systemUi08,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                if (organization != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CamstudyText(
                        text = organization,
                        style = CamstudyTheme.typography.titleSmall.copy(
                            color = CamstudyTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            if (introduce != null) {
                Spacer(modifier = Modifier.height(4.dp))
                CamstudyText(
                    text = introduce,
                    style = CamstudyTheme.typography.labelMedium.copy(
                        color = CamstudyTheme.colorScheme.systemUi04,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            CamstudyText(
                text = tags.map { "#$it" }.reduce { acc, s -> "$acc $s" },
                style = CamstudyTheme.typography.labelMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi06,
                    fontWeight = FontWeight.Normal
                )
            )
        }
        CamstudyIcon(
            modifier = Modifier.size(width = 13.dp, height = 16.dp),
            icon = CamstudyIcons.ArrowForward,
            contentDescription = null,
            tint = CamstudyTheme.colorScheme.systemUi07
        )
    }
}

@Composable
fun UserTileShimmer() {
    val shimmerColor = CamstudyTheme.colorScheme.systemUi01
    val shape = RoundedCornerShape(4.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(ProfileSize)
                .clip(ProfileShape)
                .background(color = shimmerColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Box(
                Modifier
                    .size(width = 134.dp, height = 22.dp)
                    .clip(shape)
                    .background(color = shimmerColor)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                Modifier
                    .size(width = 148.dp, height = 16.dp)
                    .clip(shape)
                    .background(color = shimmerColor)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                Modifier
                    .size(width = 88.dp, height = 16.dp)
                    .clip(shape)
                    .background(color = shimmerColor)
            )
        }
    }
}

@Preview
@Composable
private fun UserTilePreview() {
    CamstudyTheme {
        UserTile(
            name = "김민성",
            profileImageUrl = null,
            organization = "한성대학교",
            introduce = "Hi there!",
            tags = listOf("Android", "Dev"),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun UserTileShimmerPreview() {
    CamstudyTheme {
        UserTileShimmer()
    }
}
