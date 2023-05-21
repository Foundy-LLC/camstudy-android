package io.foundy.setting.ui.profile

import android.graphics.Bitmap

data class EditProfileUiState(
    val didBind: Boolean = false,
    val name: String = "",
    val introduce: String = "",
    val imageUrl: String? = null,
    val selectedImage: Bitmap? = null,
    val tags: List<String> = emptyList()
)
