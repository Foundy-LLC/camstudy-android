package io.foundy.feature.welcome.ui

import android.graphics.Bitmap
import androidx.annotation.StringRes
import io.foundy.core.model.Tag
import io.foundy.core.model.constant.UserConstants

data class WelcomeUiState(
    val nameInput: String = "",
    val selectedProfileImage: Bitmap? = null,
    val introduceInput: String = "",
    val tagInput: String = "",
    val recommendedTags: List<Tag> = emptyList(),
    val addedTags: List<String> = emptyList(),
    val didFocusNameInput: Boolean = false,
    @StringRes val nameErrorMessageRes: Int? = null,
    @StringRes val tagErrorMessageRes: Int? = null,
    val inSaving: Boolean = false
) {
    val enableTagInput: Boolean
        get() = addedTags.size < UserConstants.MaxTagCount

    val enabledAddTagButton: Boolean
        get() = addedTags.size < UserConstants.MaxTagCount &&
            tagInput.isNotBlank() &&
            !addedTags.contains(tagInput)

    val enabledDoneButton: Boolean
        get() = nameInput.isNotEmpty() && addedTags.isNotEmpty() && !inSaving

    @get:StringRes
    val doneButtonTextRes: Int
        get() {
            if (inSaving) {
                return R.string.saving
            }
            return R.string.done
        }
}
