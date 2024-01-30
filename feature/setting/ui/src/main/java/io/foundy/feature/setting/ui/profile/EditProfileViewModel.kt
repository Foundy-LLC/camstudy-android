package io.foundy.feature.setting.ui.profile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.common.util.ConvertBitmapToFileUseCase
import io.foundy.core.model.constant.UserConstants
import io.foundy.core.ui.UserMessage
import io.foundy.feature.setting.ui.R
import io.foundy.feature.setting.ui.model.EditProfileResult
import io.foundy.feature.user.domain.repository.UserRepository
import io.foundy.feature.welcome.data.repository.WelcomeRepository
import kotlinx.coroutines.CancellationException
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
class EditProfileViewModel @Inject constructor(
    private val welcomeRepository: WelcomeRepository,
    private val userRepository: UserRepository,
    private val convertBitmapToFileUseCase: ConvertBitmapToFileUseCase
) : ViewModel(), ContainerHost<EditProfileUiState, EditProfileSideEffect> {

    override val container: Container<EditProfileUiState, EditProfileSideEffect> = container(
        EditProfileUiState(
            onNameChange = ::changeName,
            onIntroduceChange = ::changeIntroduce,
            onTagChange = ::changeTagInput,
            onTagAdd = ::addTag,
            onTagRemove = ::removeTag,
            onSaveClick = ::save,
            onUseDefaultImageClick = ::useDefaultImage,
            onSelectImage = ::changeProfileImage
        )
    )

    private var recommendedTagsFetchJob: Job? = null

    fun bind(
        name: String,
        introduce: String,
        imageUrl: String?,
        tags: List<String>
    ) = intent {
        if (state.didBind) {
            return@intent
        }
        reduce {
            state.copy(
                didBind = true,
                name = name,
                introduce = introduce,
                imageUrl = imageUrl,
                tags = tags,
                previousName = name,
                previousIntroduce = introduce,
                previousTags = tags,
                previousImageUrl = imageUrl
            )
        }
    }

    private fun changeName(name: String) = blockingIntent {
        reduce { state.copy(name = name) }
    }

    private fun changeIntroduce(introduce: String) = blockingIntent {
        reduce { state.copy(introduce = introduce) }
    }

    private fun changeTagInput(tagInput: String) = blockingIntent {
        if (state.isTagFull || tagInput.length > UserConstants.MaxTagLength) {
            return@blockingIntent
        }
        reduce { state.copy(tagInput = tagInput) }
        if (tagInput.isEmpty()) {
            reduce { state.copy(recommendedTags = emptyList()) }
        } else {
            fetchRecommendedTags(userTagInput = tagInput)
        }
    }

    private fun addTag(tag: String) = intent {
        reduce { state.copy(tags = state.tags + tag) }
    }

    private fun removeTag(tag: String) = intent {
        reduce { state.copy(tags = state.tags - tag) }
    }

    private fun useDefaultImage() = intent {
        reduce { state.copy(imageUrl = null, selectedImage = null) }
    }

    private fun changeProfileImage(bitmap: Bitmap?) = intent {
        // TODO: 이미지 크기 제한하기
        reduce { state.copy(selectedImage = bitmap) }
    }

    private fun fetchRecommendedTags(userTagInput: String) = intent {
        recommendedTagsFetchJob?.cancel()
        recommendedTagsFetchJob = viewModelScope.launch {
            delay(300)
            welcomeRepository.getTags(userTagInput)
                .onSuccess { tags ->
                    reduce {
                        state.copy(
                            recommendedTags = tags.map { it.name }
                                .filterNot { state.tags.contains(it) }
                        )
                    }
                }.onFailure {
                    if (it is CancellationException) {
                        return@onFailure
                    }
                    postSideEffect(
                        EditProfileSideEffect.ErrorMessage(
                            UserMessage(
                                content = it.message,
                                defaultRes = R.string.failed_to_load_recommended_user_tags
                            )
                        )
                    )
                }
        }
    }

    private fun save() = intent {
        assert(state.canSave)
        reduce { state.copy(isInSaving = true) }
        userRepository.updateUserProfile(
            name = state.name,
            introduce = state.introduce.ifEmpty { null },
            tags = state.tags,
            profileImage = state.selectedImage?.let {
                convertBitmapToFileUseCase(it, fileName = "user_profile.png")
            },
            shouldRemoveProfileImage = state.shouldRemoveProfileImage
        ).onSuccess { newImageUrl ->
            postSideEffect(
                EditProfileSideEffect.SuccessToSave(
                    result = EditProfileResult(
                        name = state.name,
                        introduce = state.introduce.ifEmpty { null },
                        tags = StringList(state.tags),
                        /*
                         * - 기본 -> 기본: 둘 다 null
                         * - 기본 -> 새 이미지: newImageUrl 있음
                         * - 이미지 -> 기본: shouldRemoveProfileImage가 true
                         * - 이미지 -> 새 이미지: newImageUrl 있음
                         * - 이미지 -> 이미지: newImageUrl 없음, imageUrl있음
                         */
                        profileImage = newImageUrl ?: if (state.shouldRemoveProfileImage) {
                            null
                        } else {
                            state.imageUrl
                        }
                    )
                )
            )
        }.onFailure {
            postSideEffect(
                EditProfileSideEffect.ErrorMessage(
                    UserMessage(
                        content = it.message,
                        defaultRes = R.string.failed_to_save_user_profile
                    )
                )
            )
        }
        reduce { state.copy(isInSaving = false) }
    }
}
