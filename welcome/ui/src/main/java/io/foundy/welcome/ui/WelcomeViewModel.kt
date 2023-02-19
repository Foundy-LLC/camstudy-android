package io.foundy.welcome.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.model.constant.MAX_INTRODUCE_LENGTH
import io.foundy.core.model.constant.MAX_NAME_LENGTH
import io.foundy.core.model.constant.MAX_TAG_COUNT
import io.foundy.core.model.constant.MAX_TAG_LENGTH
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor() :
    ViewModel(), ContainerHost<WelcomeUiState, WelcomeSideEffect> {

    override val container: Container<WelcomeUiState, WelcomeSideEffect> =
        container(WelcomeUiState())

    fun updateNameInput(name: String) = intent {
        if (name.length > MAX_NAME_LENGTH) {
            return@intent
        }
        reduce { state.copy(nameInput = name) }
        updateNameErrorMessage()
    }

    fun updateIntroduceInput(introduce: String) = intent {
        if (introduce.length > MAX_INTRODUCE_LENGTH) {
            return@intent
        }
        reduce { state.copy(introduceInput = introduce) }
    }

    fun updateTagInput(tag: String) = intent {
        if (tag.length > MAX_TAG_LENGTH) {
            return@intent
        }
        reduce { state.copy(tagInput = tag) }
    }

    fun addTag() = intent {
        check(state.addedTags.size < MAX_TAG_COUNT)
        val newTags = state.addedTags + state.tagInput
        reduce { state.copy(tagInput = "", addedTags = newTags) }
        updateTagErrorMessage()
    }

    fun removeTag(tag: String) = intent {
        val newTags = state.addedTags.filter { it != tag }
        reduce { state.copy(addedTags = newTags) }
        updateTagErrorMessage()
    }

    fun updateNameErrorMessage() = intent {
        var messageRes: Int? = null
        if (state.nameInput.isEmpty()) {
            messageRes = R.string.must_input_name_error_message
        }
        reduce { state.copy(nameErrorMessageRes = messageRes) }
    }

    fun updateTagErrorMessage() = intent {
        var messageRes: Int? = null
        if (state.addedTags.isEmpty()) {
            messageRes = R.string.must_add_tag_error_message
        }
        reduce { state.copy(tagErrorMessageRes = messageRes) }
    }

    fun saveInitInformation() = intent {
        TODO()
    }
}
