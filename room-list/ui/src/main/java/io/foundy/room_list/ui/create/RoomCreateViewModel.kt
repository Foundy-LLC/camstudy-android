package io.foundy.room_list.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.model.constant.RoomConstants
import io.foundy.core.model.constant.UserConstants
import io.foundy.core.ui.UserMessage
import io.foundy.room_list.data.model.RoomCreateRequestBody
import io.foundy.room_list.data.repository.RoomListRepository
import io.foundy.room_list.ui.R
import io.foundy.welcome.data.repository.WelcomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class RoomCreateViewModel @Inject constructor(
    private val welcomeRepository: WelcomeRepository,
    private val roomListRepository: RoomListRepository,
    private val authRepository: AuthRepository
) : ViewModel(), ContainerHost<RoomCreateUiState, RoomCreateSideEffect> {

    override val container: Container<RoomCreateUiState, RoomCreateSideEffect> = container(
        RoomCreateUiState(
            onTitleChange = ::updateTitle,
            onRemoveTag = ::removeTag,
            onAddTag = ::addTag,
            onTagChange = ::updateTagInput,
            onThumbnailChange = { /* TODO: 디자인 나오면 구현하기 */ },
            onPasswordChange = ::updatePassword,
            onCreateClick = ::saveRoom
        )
    )

    private var recommendedTagsFetchJob: Job? = null

    private fun updateTitle(title: String) = intent {
        reduce { state.copy(title = title.filterNot { it == '\n' }) }
    }

    private fun updateTagInput(tag: String) = intent {
        if (state.isTagFull || tag.length > UserConstants.MaxTagLength) {
            return@intent
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

    private fun updatePassword(password: String?) = intent {
        reduce { state.copy(password = password) }
    }

    private fun saveRoom() = intent {
        check(state.canCreate)
        reduce { state.copy(isInCreating = true) }
        roomListRepository.createRoom(
            createRequestBody = RoomCreateRequestBody(
                masterId = requireNotNull(authRepository.currentUserIdStream.firstOrNull()) {
                    "로그인 하지 않고 방을 생성하려 했습니다."
                },
                title = state.title,
                password = state.password,
                tags = state.addedTags,
                timer = RoomConstants.TimerDefault,
                shortBreak = RoomConstants.ShortBreakDefault,
                longBreak = RoomConstants.LongBreakDefault,
                longBreakInterval = RoomConstants.LongBreakIntervalDefault,
                expiredAt = get30DaysAfterDate().toISOString()
            ),
            thumbnail = null // TODO: 구현하기
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
