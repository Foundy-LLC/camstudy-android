package io.foundy.room_list.ui.create

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import io.foundy.core.model.constant.RoomConstants
import io.foundy.room_list.ui.R

data class RoomCreateUiState(
    val title: String = "",
    val password: String? = null,
    val thumbnail: Bitmap? = null,
    val tag: String = "",
    val recommendedTags: List<String> = emptyList(),
    val addedTags: List<String> = emptyList(),
    val onTitleChange: (String) -> Unit,
    val onPasswordChange: (String?) -> Unit,
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

    val isExceedPasswordLength: Boolean
        get() {
            if (password == null) {
                return false
            }
            return password.length > RoomConstants.MaxPasswordLength
        }

    private val isPasswordLengthValid: Boolean
        get() {
            if (password == null) {
                return true
            }
            return password.isNotEmpty() && !isExceedPasswordLength
        }

    val passwordSupportingTextRes: String
        @Composable
        @ReadOnlyComposable
        get() {
            if (isExceedPasswordLength) {
                return stringResource(
                    id = R.string.room_password_max_length_error,
                    RoomConstants.MaxPasswordLength
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
