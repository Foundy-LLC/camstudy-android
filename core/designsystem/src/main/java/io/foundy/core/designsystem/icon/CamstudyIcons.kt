package io.foundy.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

object CamstudyIcons {
    val Home = Icons.Default.Home
    val HomeOutlined = Icons.Outlined.Home
}

sealed class CamstudyIcon
data class ImageVectorIcon(val imageVector: ImageVector) : CamstudyIcon()
data class DrawableResourceIcon(@DrawableRes val id: Int) : CamstudyIcon()

fun ImageVector.asCamstudyIcon(): CamstudyIcon {
    return ImageVectorIcon(this)
}

fun @receiver:DrawableRes Int.asCamstudyIcon() : CamstudyIcon {
    return DrawableResourceIcon(this)
}
