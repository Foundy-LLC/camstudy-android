package io.foundy.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwitchVideo
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.sharp.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import io.foundy.core.designsystem.R

object CamstudyIcons {
    val StudyRoom = R.drawable.ic_study_room.asCamstudyIcon()
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
    val Delete = Icons.Default.Delete.asCamstudyIcon()
    val Person = Icons.Default.Person.asCamstudyIcon()
    val PersonAdd = Icons.Default.PersonAdd.asCamstudyIcon()
    val PersonRemove = Icons.Default.PersonRemove.asCamstudyIcon()
    val People = Icons.Default.People.asCamstudyIcon()
    val Timer = Icons.Default.Timer.asCamstudyIcon()
    val Search = Icons.Default.Search.asCamstudyIcon()
    val LockSharp = Icons.Sharp.Lock.asCamstudyIcon()
}

sealed class CamstudyIcon
data class ImageVectorIcon(val imageVector: ImageVector) : CamstudyIcon()
data class DrawableResourceIcon(@DrawableRes val id: Int) : CamstudyIcon()

private fun ImageVector.asCamstudyIcon(): CamstudyIcon {
    return ImageVectorIcon(this)
}

private fun @receiver:DrawableRes Int.asCamstudyIcon(): CamstudyIcon {
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
