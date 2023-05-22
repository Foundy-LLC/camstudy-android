package io.foundy.setting.ui.profile

import io.foundy.core.ui.UserMessage

sealed class EditProfileSideEffect {

    object SuccessToSave : EditProfileSideEffect()

    data class ErrorMessage(val message: UserMessage) : EditProfileSideEffect()
}
