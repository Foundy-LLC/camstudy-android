package io.foundy.ranking.ui

import io.foundy.core.ui.UserMessage

sealed class RankingSideEffect {

    data class ErrorMessage(val message: UserMessage) : RankingSideEffect()
}
