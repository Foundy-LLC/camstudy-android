package io.foundy.core.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme

@OptIn(ExperimentalCoilApi::class)
fun Context.clearImageCache(model: Any) {
    val imageLoader = this.imageLoader
    imageLoader.diskCache?.remove(model.toString())
    imageLoader.memoryCache?.remove(MemoryCache.Key(model.toString()))
}

@Composable
fun UserProfileImage(
    model: Any?,
    imageOrContainerSize: Dp = 40.dp,
    fallbackIconSize: Dp = 24.dp,
    cornerShape: Shape = RoundedCornerShape(8.dp)
) {
    val thumbnailModifier = Modifier
        .size(imageOrContainerSize)
        .clip(cornerShape)
    val context = LocalContext.current

    if (model != null) {
        Image(
            modifier = thumbnailModifier,
            painter = rememberAsyncImagePainter(
                remember(model) {
                    ImageRequest.Builder(context)
                        .data(model)
                        // TODO: model이 Bitmap 객체인 경우도 캐시를 해야하나?
                        .diskCacheKey(model.toString())
                        .memoryCacheKey(model.toString())
                        .build()
                }
            ),
            contentScale = ContentScale.Crop,
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
