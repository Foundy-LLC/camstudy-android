package io.foundy.setting.ui.model

import android.os.Parcelable
import io.foundy.setting.ui.profile.StringList
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditProfileResult(
    val name: String,
    val introduce: String?,
    val profileImage: String?,
    val tags: StringList
) : Parcelable
