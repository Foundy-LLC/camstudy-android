package io.foundy.setting.ui.profile

import io.foundy.core.ui.UserMessage
import io.foundy.setting.ui.model.EditProfileResult

sealed class EditProfileSideEffect {

    data class SuccessToSave(val result: EditProfileResult) : EditProfileSideEffect()

    data class ErrorMessage(val message: UserMessage) : EditProfileSideEffect()
}
