package io.foundy.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun UserProfileImage(
    imageUrl: String?,
    imageOrContainerSize: Dp = 40.dp,
    fallbackIconSize: Dp = 24.dp,
    cornerShape: Shape = RoundedCornerShape(8.dp)
) {
    val thumbnailModifier = Modifier
        .size(imageOrContainerSize)
        .clip(cornerShape)

    if (imageUrl != null) {
        AsyncImage(
            modifier = thumbnailModifier,
            model = imageUrl,
            contentDescription = null
        )
    } else {
        Box(
            modifier = thumbnailModifier.background(color = CamstudyTheme.colorScheme.systemUi01)
        ) {
            CamstudyIcon(
                modifier = Modifier
                    .size(fallbackIconSize)
                    .align(Alignment.Center),
                icon = CamstudyIcons.Person,
                tint = CamstudyTheme.colorScheme.systemUi03,
                contentDescription = null
            )
        }
    }
}
