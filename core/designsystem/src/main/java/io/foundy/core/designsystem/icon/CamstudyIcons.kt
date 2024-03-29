package io.foundy.core.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CancelScheduleSend
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.sharp.BusinessCenter
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
    val ArrowBack = Icons.Default.ArrowBack.asCamstudyIcon()
    val ArrowForward = Icons.Default.ArrowForwardIos.asCamstudyIcon()
    val StudyRoom = R.drawable.ic_study_room.asCamstudyIcon()
    val RoomDefault = R.drawable.ic_room_default.asCamstudyIcon()
    val VideoCam = R.drawable.ic_video.asCamstudyIcon()
    val VideoCamOff = R.drawable.ic_video_off.asCamstudyIcon()
    val MaterialVideoCamOff = Icons.Default.VideocamOff.asCamstudyIcon()
    val Mic = R.drawable.ic_mic.asCamstudyIcon()
    val MicOff = R.drawable.ic_mic_off.asCamstudyIcon()
    val Home = R.drawable.ic_home.asCamstudyIcon()
    val Headset = R.drawable.ic_headset.asCamstudyIcon()
    val HeadsetOff = R.drawable.ic_headset_off.asCamstudyIcon()
    val StartTimer = R.drawable.ic_start_timer.asCamstudyIcon()
    val StartTimerPressed = R.drawable.ic_start_timer_pressed.asCamstudyIcon()
    val EmptyCrop = R.drawable.ic_empty_crop.asCamstudyIcon()
    val Question = R.drawable.ic_empty_crop.asCamstudyIcon()
    val Crop = R.drawable.ic_crop.asCamstudyIcon()
    val Ranking = R.drawable.ic_ranking.asCamstudyIcon()
    val MoreHoriz = Icons.Default.MoreHoriz.asCamstudyIcon()
    val Done = Icons.Default.Done.asCamstudyIcon()
    val AccessTimeFilled = Icons.Default.AccessTimeFilled.asCamstudyIcon()
    val Leaf = R.drawable.ic_leaf.asCamstudyIcon()
    val FlipCamera = Icons.Default.FlipCameraAndroid.asCamstudyIcon()
    val Chat = Icons.Default.Chat.asCamstudyIcon()
    val Send = Icons.Default.Send.asCamstudyIcon()
    val Add = Icons.Default.Add.asCamstudyIcon()
    val Delete = Icons.Default.Delete.asCamstudyIcon()
    val Error = Icons.Default.Error.asCamstudyIcon()
    val Person = Icons.Default.Person.asCamstudyIcon()
    val NoAccounts = Icons.Default.NoAccounts.asCamstudyIcon()
    val PersonAdd = Icons.Default.PersonAdd.asCamstudyIcon()
    val PersonRemove = Icons.Default.PersonRemove.asCamstudyIcon()
    val CancelScheduleSend = Icons.Default.CancelScheduleSend.asCamstudyIcon()
    val PersonOff = Icons.Default.PersonOff.asCamstudyIcon()
    val BusinessCenter = Icons.Sharp.BusinessCenter.asCamstudyIcon()
    val People = Icons.Default.People.asCamstudyIcon()
    val Timer = Icons.Outlined.Timer.asCamstudyIcon()
    val Search = Icons.Default.Search.asCamstudyIcon()
    val Close = Icons.Default.Close.asCamstudyIcon()
    val LockSharp = Icons.Sharp.Lock.asCamstudyIcon()
    val KeyboardArrowUp = Icons.Default.KeyboardArrowUp.asCamstudyIcon()
    val KeyboardArrowDown = Icons.Default.KeyboardArrowDown.asCamstudyIcon()
    val AppTitle = R.drawable.ic_app_title.asCamstudyIcon()
    val LoginTitle = R.drawable.ic_login_title.asCamstudyIcon()
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
