package io.foundy.feature.room_list.ui.create

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import io.foundy.core.model.constant.RoomConstants
import io.foundy.feature.room_list.ui.R

data class RoomCreateUiState(
    val title: String = "",
    val isPrivate: Boolean = false,
    val password: String = "",
    val thumbnail: Bitmap? = null,
    val tag: String = "",
    val recommendedTags: List<String> = emptyList(),
    val addedTags: List<String> = emptyList(),
    val onTitleChange: (String) -> Unit,
    val onIsPrivateChange: (Boolean) -> Unit,
    val onPasswordChange: (String) -> Unit,
    val onTagChange: (String) -> Unit,
    val onThumbnailChange: (Bitmap?) -> Unit,
    val onAddTag: (String) -> Unit,
    val onRemoveTag: (String) -> Unit,
    val onCreateClick: () -> Unit,
    val isInCreating: Boolean = false
) {
    private val isTitleLengthValid: Boolean
        get() = title.isNotEmpty() && !isExceedTitleLength

    val isExceedTitleLength: Boolean
        get() = title.length > RoomConstants.MaxTitleLength

    val titleSupportingTextRes: String
        @Composable
        @ReadOnlyComposable
        get() {
            if (isExceedTitleLength) {
                return stringResource(
                    id = R.string.room_title_max_length_error,
                    RoomConstants.MaxTitleLength
                )
            }
            return stringResource(id = R.string.room_title_supporting_text)
        }

    val isPasswordLengthValid: Boolean
        get() {
            if (!isPrivate) {
                return true
            }
            return RoomConstants.PasswordRange.contains(password.length)
        }

    val passwordSupportingTextRes: String
        @Composable
        @ReadOnlyComposable
        get() {
            if (!isPasswordLengthValid) {
                return stringResource(
                    id = R.string.room_password_length_error,
                    RoomConstants.PasswordRange.first,
                    RoomConstants.PasswordRange.last
                )
            }
            return stringResource(id = R.string.room_password_supporting_text)
        }

    private val hasTags: Boolean
        get() = addedTags.isNotEmpty()

    val isTagFull: Boolean
        get() = addedTags.size == RoomConstants.MaxTagCount

    val canCreate: Boolean
        get() {
            return isTitleLengthValid &&
                isPasswordLengthValid &&
                hasTags &&
                !isInCreating
        }
}
