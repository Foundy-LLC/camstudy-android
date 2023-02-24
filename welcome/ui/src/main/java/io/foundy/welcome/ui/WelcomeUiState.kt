package io.foundy.welcome.ui

import android.graphics.Bitmap
import androidx.annotation.StringRes
import io.foundy.core.model.constant.UserConstants

// TODO: 제출하고 로딩중일 때 로딩을 나타내는 필드 추가하기
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
        get() = addedTags.size < UserConstants.MaxTagCount

    val enabledAddTagButton: Boolean
        get() = addedTags.size < UserConstants.MaxTagCount &&
            tagInput.isNotBlank() &&
            !addedTags.contains(tagInput)

    val enabledDoneButton: Boolean
        get() = nameInput.isNotEmpty() && addedTags.isNotEmpty()
}
