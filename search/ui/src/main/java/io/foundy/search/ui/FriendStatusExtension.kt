package io.foundy.search.ui

import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.model.FriendStatus

val FriendStatus.buttonIcon: CamstudyIcon
    get() = when (this) {
        FriendStatus.NONE -> CamstudyIcons.PersonAdd
        FriendStatus.REQUESTED -> CamstudyIcons.Pending
        FriendStatus.ACCEPTED -> CamstudyIcons.PersonRemove
    }
