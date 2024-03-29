package io.foundy.feature.room_list.ui.create

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.core.common.util.ConvertBitmapToFileUseCase
import io.foundy.core.model.constant.RoomConstants
import io.foundy.core.model.constant.UserConstants
import io.foundy.core.ui.UserMessage
import io.foundy.feature.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.feature.room_list.data.model.RoomCreateRequestBody
import io.foundy.feature.room_list.data.repository.RoomListRepository
import io.foundy.feature.room_list.ui.R
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(OrbitExperimental::class)
@HiltViewModel
class RoomCreateViewModel @Inject constructor(
    private val welcomeRepository: WelcomeRepository,
    private val roomListRepository: RoomListRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val convertBitmapToFileUseCase: ConvertBitmapToFileUseCase
) : ViewModel(), ContainerHost<RoomCreateUiState, RoomCreateSideEffect> {

    override val container: Container<RoomCreateUiState, RoomCreateSideEffect> = container(
        RoomCreateUiState(
            onTitleChange = ::updateTitle,
            onRemoveTag = ::removeTag,
            onAddTag = ::addTag,
            onTagChange = ::updateTagInput,
            onThumbnailChange = ::updateThumbnail,
            onPasswordChange = ::updatePassword,
            onCreateClick = ::saveRoom,
            onIsPrivateChange = ::updateIsPrivate
        )
    )

    private var recommendedTagsFetchJob: Job? = null

    private fun updateThumbnail(thumbnail: Bitmap?) = intent {
        reduce { state.copy(thumbnail = thumbnail) }
    }

    private fun updateTitle(title: String) = blockingIntent {
        reduce { state.copy(title = title.filterNot { it == '\n' }) }
    }

    private fun updateTagInput(tag: String) = blockingIntent {
        if (state.isTagFull || tag.length > UserConstants.MaxTagLength) {
            return@blockingIntent
        }
        reduce { state.copy(tag = tag) }
        if (tag.isEmpty()) {
            reduce { state.copy(recommendedTags = emptyList()) }
        } else {
            fetchRecommendedTags(userTagInput = tag)
        }
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
                                .filterNot { state.addedTags.contains(it) }
                        )
                    }
                }.onFailure {
                    if (it is CancellationException) {
                        return@onFailure
                    }
                    postSideEffect(
                        RoomCreateSideEffect.ErrorMessage(
                            UserMessage(
                                content = it.message,
                                defaultRes = R.string.failed_to_load_recommended_tags
                            )
                        )
                    )
                }
        }
    }

    private fun addTag(tag: String) = intent {
        reduce { state.copy(addedTags = state.addedTags + tag) }
    }

    private fun removeTag(tag: String) = intent {
        reduce { state.copy(addedTags = state.addedTags.filter { it != tag }) }
    }

    private fun updatePassword(password: String) = blockingIntent {
        reduce { state.copy(password = password) }
    }

    private fun updateIsPrivate(isPrivate: Boolean) = intent {
        reduce { state.copy(isPrivate = isPrivate) }
    }

    private fun saveRoom() = intent {
        check(state.canCreate)
        reduce { state.copy(isInCreating = true) }
        roomListRepository.createRoom(
            createRequestBody = RoomCreateRequestBody(
                masterId = requireNotNull(getCurrentUserIdUseCase()) {
                    "로그인 하지 않고 방을 생성하려 했습니다."
                },
                title = state.title,
                password = if (state.isPrivate) state.password else null,
                tags = state.addedTags,
                timer = RoomConstants.TimerDefault,
                shortBreak = RoomConstants.ShortBreakDefault,
                longBreak = RoomConstants.LongBreakDefault,
                longBreakInterval = RoomConstants.LongBreakIntervalDefault,
                expiredAt = get30DaysAfterDate().toISOString()
            ),
            thumbnail = state.thumbnail?.let { convertBitmapToFileUseCase(it, "thumbnail.png") }
        ).onSuccess { room ->
            postSideEffect(RoomCreateSideEffect.SuccessToCreate(room))
        }.onFailure {
            postSideEffect(
                RoomCreateSideEffect.ErrorMessage(
                    UserMessage(
                        content = it.message,
                        defaultRes = R.string.failed_to_create_room
                    )
                )
            )
        }
        reduce { state.copy(isInCreating = false) }
    }

    private fun get30DaysAfterDate(): Date {
        val millis = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)
        // 해당 날짜에서 시간이 0시 0분인 날짜를 반환한다.
        return Date(millis - (millis % (1000 * 60 * 60 * 24)))
    }

    private fun Date.toISOString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return dateFormat.format(this)
    }
}
