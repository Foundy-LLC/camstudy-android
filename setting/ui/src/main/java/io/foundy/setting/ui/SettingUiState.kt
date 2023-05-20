package io.foundy.setting.ui

import io.foundy.core.model.User
import io.foundy.core.ui.UserMessage

sealed class SettingUiState {

    object Loading : SettingUiState()

    data class Success(val currentUser: User) : SettingUiState()

    data class Failure(val message: UserMessage) : SettingUiState()
}
