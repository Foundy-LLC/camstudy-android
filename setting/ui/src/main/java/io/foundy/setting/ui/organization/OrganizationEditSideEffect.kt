package io.foundy.setting.ui.organization

import io.foundy.core.ui.UserMessage

sealed class OrganizationEditSideEffect {

    data class Message(val userMessage: UserMessage) : OrganizationEditSideEffect()
}
