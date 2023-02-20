package io.foundy.welcome.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.common.util.ConvertBitmapToFileUseCase
import io.foundy.core.model.constant.MAX_INTRODUCE_LENGTH
import io.foundy.core.model.constant.MAX_NAME_LENGTH
import io.foundy.core.model.constant.MAX_TAG_COUNT
import io.foundy.core.model.constant.MAX_TAG_LENGTH
import io.foundy.user.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val convertBitmapToFileUseCase: ConvertBitmapToFileUseCase
) : ViewModel(), ContainerHost<WelcomeUiState, WelcomeSideEffect> {

    override val container: Container<WelcomeUiState, WelcomeSideEffect> =
        container(WelcomeUiState())

    fun updateSelectedProfileImage(image: Bitmap) = intent {
        reduce { state.copy(selectedProfileImage = image) }
    }

    fun removeSelectedProfileImage() = intent {
        reduce { state.copy(selectedProfileImage = null) }
    }

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
        val uid = authRepository.currentUserIdStream.first()
        check(uid != null)
        userRepository.postUserInitialInfo(
            userId = uid,
            profileImage = state.selectedProfileImage?.let {
                convertBitmapToFileUseCase(
                    it,
                    "profileImage.png"
                )
            },
            name = state.nameInput,
            introduce = state.introduceInput,
            tags = state.addedTags
        ).onSuccess {
            postSideEffect(WelcomeSideEffect.NavigateToHome)
        }.onFailure {
            it.printStackTrace()
            postSideEffect(
                WelcomeSideEffect.Message(
                    content = it.message,
                    defaultContentRes = R.string.cannot_save_from_unknown_error
                )
            )
        }
    }
}
