package io.foundy.welcome.ui

import android.graphics.Bitmap
import androidx.annotation.StringRes
import io.foundy.core.model.constant.MAX_TAG_COUNT

data class WelcomeUiState(
    val nameInput: String = "",
    val selectedProfileImage: Bitmap? = null,
    val introduceInput: String = "",
    val tagInput: String = "",
    val addedTags: List<String> = emptyList(),
    val didFocusNameInput: Boolean = false,
    @StringRes val nameErrorMessageRes: Int? = null,
    @StringRes val tagErrorMessageRes: Int? = null
) {
    val enableTagInput: Boolean
        get() = addedTags.size < MAX_TAG_COUNT

    val enabledAddTagButton: Boolean
        get() = addedTags.size < MAX_TAG_COUNT &&
            tagInput.isNotBlank() &&
            !addedTags.contains(tagInput)

    val enabledDoneButton: Boolean
        get() = nameInput.isNotEmpty() && addedTags.isNotEmpty()
}
