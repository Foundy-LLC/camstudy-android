package io.foundy.setting.ui.profile

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import io.foundy.core.model.constant.UserConstants
import io.foundy.setting.ui.R

data class EditProfileUiState(
    val previousName: String = "",
    val previousIntroduce: String = "",
    val previousTags: List<String> = emptyList(),
    val previousImageUrl: String? = null,
    val didBind: Boolean = false,

    val name: String = "",
    val introduce: String = "",
    val imageUrl: String? = null,
    val selectedImage: Bitmap? = null,
    val tags: List<String> = emptyList(),
    val tagInput: String = "",
    val recommendedTags: List<String> = emptyList(),
    val isInSaving: Boolean = false,

    val onNameChange: (String) -> Unit,
    val onIntroduceChange: (String) -> Unit,
    val onUseDefaultImageClick: () -> Unit,
    val onSelectImage: (Bitmap?) -> Unit,
    val onTagChange: (String) -> Unit,
    val onTagAdd: (String) -> Unit,
    val onTagRemove: (String) -> Unit,
    val onSaveClick: () -> Unit
) {
    private val isNameLengthValid: Boolean
        get() = name.isNotEmpty() && !isExceedNameLength

    private val hasNameValidCharacterSet: Boolean
        get() = UserConstants.hasNameValidCharacterSet(name)

    private val isExceedNameLength: Boolean
        get() = name.length > UserConstants.MaxNameLength

    private val isExceedIntroduceLength: Boolean
        get() = introduce.length > UserConstants.MaxIntroduceLength

    private val isImageEdited: Boolean
        get() {
            return previousImageUrl != imageUrl || selectedImage != null
        }

    val shouldRemoveProfileImage: Boolean
        get() {
            return previousImageUrl != null && imageUrl == null && selectedImage == null
        }

    val isEdited: Boolean
        get() {
            return previousName != name ||
                previousIntroduce != introduce ||
                !(previousTags.containsAll(tags) && previousTags.size == tags.size) ||
                isImageEdited
        }

    val shouldShowNameError: Boolean
        get() = isExceedNameLength || !hasNameValidCharacterSet

    val shouldShowIntroduceError: Boolean
        get() = isExceedIntroduceLength

    val isTagFull: Boolean
        get() = tags.size == UserConstants.MaxTagCount

    val nameSupportingTextRes: String
        @Composable
        @ReadOnlyComposable
        get() {
            if (isExceedNameLength) {
                return stringResource(
                    R.string.name_is_exceed_supporting_text,
                    UserConstants.MaxNameLength
                )
            }
            return stringResource(R.string.name_supporting_text)
        }

    val introduceSupportingTextRes: String
        @Composable
        @ReadOnlyComposable
        get() {
            if (isExceedIntroduceLength) {
                return stringResource(
                    R.string.introduce_is_exceed_supporting_text,
                    UserConstants.MaxIntroduceLength
                )
            }
            return stringResource(
                R.string.introduce_supporting_text,
                UserConstants.MaxIntroduceLength
            )
        }

    val tagSupportingTextRes: String
        @Composable
        @ReadOnlyComposable
        get() {
            if (isTagFull) {
                return stringResource(R.string.user_tag_is_done)
            }
            return stringResource(R.string.user_tag_supporting_text)
        }

    val canSave: Boolean
        get() {
            return isEdited &&
                isNameLengthValid &&
                hasNameValidCharacterSet &&
                !isExceedIntroduceLength &&
                tags.isNotEmpty() &&
                !isInSaving
        }
}
