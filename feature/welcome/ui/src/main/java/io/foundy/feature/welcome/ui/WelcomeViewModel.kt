package io.foundy.feature.welcome.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.common.util.ConvertBitmapToFileUseCase
import io.foundy.core.model.constant.UserConstants
import io.foundy.feature.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.feature.user.domain.usecase.PostUserInitInfoUseCase
import io.foundy.feature.welcome.data.repository.WelcomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(OrbitExperimental::class)
@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val postUserInitInfoUseCase: PostUserInitInfoUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val welcomeRepository: WelcomeRepository,
    private val convertBitmapToFileUseCase: ConvertBitmapToFileUseCase
) : ViewModel(), ContainerHost<WelcomeUiState, WelcomeSideEffect> {

    override val container: Container<WelcomeUiState, WelcomeSideEffect> =
        container(WelcomeUiState())

    private var tagSearchJob: Job? = null

    fun updateSelectedProfileImage(image: Bitmap) = intent {
        reduce { state.copy(selectedProfileImage = image) }
    }

    fun removeSelectedProfileImage() = intent {
        reduce { state.copy(selectedProfileImage = null) }
    }

    fun updateNameInput(name: String) = blockingIntent {
        if (name.length > UserConstants.MaxNameLength) {
            return@blockingIntent
        }
        reduce { state.copy(nameInput = name) }
        updateNameErrorMessage()
    }

    fun updateIntroduceInput(introduce: String) = blockingIntent {
        if (introduce.length > UserConstants.MaxIntroduceLength) {
            return@blockingIntent
        }
        reduce { state.copy(introduceInput = introduce) }
    }

    fun updateTagInput(tag: String) = blockingIntent {
        if (tag.length > UserConstants.MaxTagLength) {
            return@blockingIntent
        }
        reduce { state.copy(tagInput = tag) }
        fetchRecommendedTags(inputTag = tag)
    }

    private fun fetchRecommendedTags(inputTag: String) = intent {
        tagSearchJob?.cancel()
        tagSearchJob = viewModelScope.launch {
            delay(300)
            welcomeRepository.getTags(name = inputTag).onSuccess { tags ->
                reduce { state.copy(recommendedTags = tags) }
            }
        }
    }

    fun addTag() = intent {
        check(state.addedTags.size < UserConstants.MaxTagCount)
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
        val uid = getCurrentUserIdUseCase()
        check(uid != null)
        reduce { state.copy(inSaving = true) }
        postUserInitInfoUseCase(
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
        reduce { state.copy(inSaving = false) }
    }
}
