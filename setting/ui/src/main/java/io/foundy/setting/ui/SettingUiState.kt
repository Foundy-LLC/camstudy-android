package io.foundy.setting.ui

import io.foundy.core.model.User

data class SettingUiState(
    val isLoading: Boolean = true,
    val currentUser: User? = null
)
