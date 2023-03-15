package io.foundy.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwitchVideo
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

object CamstudyIcons {
    val Home = Icons.Default.Home.asCamstudyIcon()
    val HomeOutlined = Icons.Outlined.Home.asCamstudyIcon()
    val VideoCam = Icons.Default.Videocam.asCamstudyIcon()
    val VideoCamOff = Icons.Default.VideocamOff.asCamstudyIcon()
    val Mic = Icons.Default.Mic.asCamstudyIcon()
    val MicOff = Icons.Default.MicOff.asCamstudyIcon()
    val Headset = Icons.Default.Headset.asCamstudyIcon()
    val HeadsetOff = Icons.Default.HeadsetOff.asCamstudyIcon()
    val MoreVert = Icons.Default.MoreVert.asCamstudyIcon()
    val SwitchVideo = Icons.Default.SwitchVideo.asCamstudyIcon()
    val Chat = Icons.Default.Chat.asCamstudyIcon()
    val Send = Icons.Default.Send.asCamstudyIcon()
}

sealed class CamstudyIcon
data class ImageVectorIcon(val imageVector: ImageVector) : CamstudyIcon()
data class DrawableResourceIcon(@DrawableRes val id: Int) : CamstudyIcon()

fun ImageVector.asCamstudyIcon(): CamstudyIcon {
    return ImageVectorIcon(this)
}

fun @receiver:DrawableRes Int.asCamstudyIcon(): CamstudyIcon {
    return DrawableResourceIcon(this)
}

@Composable
fun CamstudyIcon(
    icon: CamstudyIcon,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    when (icon) {
        is DrawableResourceIcon -> Icon(
            painter = painterResource(id = icon.id),
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
        is ImageVectorIcon -> Icon(
            imageVector = icon.imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}
