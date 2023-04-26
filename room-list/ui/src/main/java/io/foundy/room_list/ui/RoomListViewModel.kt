package io.foundy.room_list.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.common.util.ConvertBitmapToFileUseCase
import io.foundy.room_list.data.model.RoomCreateRequestBody
import io.foundy.room_list.data.repository.RoomListRepository
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
import javax.inject.Inject

@HiltViewModel
class RoomListViewModel @Inject constructor(
    private val roomListRepository: RoomListRepository,
    private val authRepository: AuthRepository,
    private val convertBitmapToFileUseCase: ConvertBitmapToFileUseCase
) : ViewModel(), ContainerHost<RoomListUiState, RoomListSideEffect> {

    override val container: Container<RoomListUiState, RoomListSideEffect> = container(
        RoomListUiState(
            roomPagingDataStream = roomListRepository.getRooms("").cachedIn(viewModelScope),
            onSearchQueryChange = ::updateSearchQuery,
            roomCreateInput = buildRoomCreateInput()
        )
    )

    private var roomSearchJob: Job? = null

    private fun updateSearchQuery(query: String) = intent {
        reduce { state.copy(searchQuery = query) }
        searchRooms(query = query)
    }

    private fun searchRooms(query: String) = intent {
        roomSearchJob?.cancel()
        roomSearchJob = viewModelScope.launch {
            delay(300)
            val roomsStream = roomListRepository.getRooms(query)
            reduce { state.copy(roomPagingDataStream = roomsStream) }
        }
    }

    private fun updateTitleInput(title: String) = intent {
        reduce { state.copy(roomCreateInput = state.roomCreateInput.copy(title = title)) }
    }

    private fun updatePasswordInput(password: String) = intent {
        reduce { state.copy(roomCreateInput = state.roomCreateInput.copy(password = password)) }
    }

    private fun updateThumbnailInput(image: Bitmap) = intent {
        reduce { state.copy(roomCreateInput = state.roomCreateInput.copy(thumbnail = image)) }
    }

    private fun updateTimerInput(length: String) = intent {
        reduce { state.copy(roomCreateInput = state.roomCreateInput.copy(timer = length.trim())) }
    }

    private fun updateShortBreakInput(length: String) = intent {
        reduce {
            state.copy(roomCreateInput = state.roomCreateInput.copy(shortBreak = length.trim()))
        }
    }

    private fun updateLongBreakInput(length: String) = intent {
        reduce {
            state.copy(roomCreateInput = state.roomCreateInput.copy(longBreak = length.trim()))
        }
    }

    private fun updateLongBreakIntervalInput(interval: String) = intent {
        reduce {
            state.copy(
                roomCreateInput = state.roomCreateInput.copy(longBreakInterval = interval.trim())
            )
        }
    }

    private fun updateExpiredDate(date: Date) = intent {
        reduce {
            state.copy(roomCreateInput = state.roomCreateInput.copy(expiredAt = date))
        }
    }

    private fun createRoom() = intent {
        val roomCreateInput = state.roomCreateInput
        check(roomCreateInput.canSave)
        roomListRepository.createRoom(
            createRequestBody = RoomCreateRequestBody(
                masterId = requireNotNull(authRepository.currentUserIdStream.firstOrNull()) {
                    "로그인 하지 않고 방을 생성하려 했습니다."
                },
                title = roomCreateInput.title,
                password = roomCreateInput.password.ifEmpty { null },
                tags = listOf(), // TODO: 사용자에게 태그 입력받아 실제 데이터 전달하기
                timer = roomCreateInput.timer.toInt(),
                shortBreak = roomCreateInput.shortBreak.toInt(),
                longBreak = roomCreateInput.longBreak.toInt(),
                longBreakInterval = roomCreateInput.longBreakInterval.toInt(),
                expiredAt = roomCreateInput.expiredAt.toISOString()
            ),
            thumbnail = roomCreateInput.thumbnail?.let {
                convertBitmapToFileUseCase(it, "thumbnail.png")
            }
        ).onSuccess { roomOverview ->
            reduce { state.copy(roomCreateInput = buildRoomCreateInput()) }
            postSideEffect(RoomListSideEffect.SuccessToCreateRoom(createdRoom = roomOverview))
        }.onFailure {
            postSideEffect(
                RoomListSideEffect.Message(
                    content = it.message,
                    defaultRes = R.string.failed_to_create_room
                )
            )
        }
    }

    private fun Date.toISOString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return dateFormat.format(this)
    }

    private fun buildRoomCreateInput(): RoomCreateInputUiState {
        return RoomCreateInputUiState(
            onTitleChange = ::updateTitleInput,
            onPasswordChange = ::updatePasswordInput,
            onThumbnailChange = ::updateThumbnailInput,
            onTimerChange = ::updateTimerInput,
            onShortBreakChange = ::updateShortBreakInput,
            onLongBreakChange = ::updateLongBreakInput,
            onLongBreakIntervalChange = ::updateLongBreakIntervalInput,
            onExpiredDateChange = ::updateExpiredDate,
            onCreateClick = ::createRoom
        )
    }
}
