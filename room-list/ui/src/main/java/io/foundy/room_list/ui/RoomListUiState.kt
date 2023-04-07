package io.foundy.room_list.ui

import android.graphics.Bitmap
import androidx.paging.PagingData
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.constant.RoomConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.lang.System.currentTimeMillis
import java.util.Date
import java.util.concurrent.TimeUnit

data class RoomListUiState(
    val roomPagingDataStream: Flow<PagingData<RoomOverview>> = emptyFlow(),
    val searchQuery: String = "",
    val onSearchQueryChange: (String) -> Unit,
    val roomCreateInput: RoomCreateInputUiState
)

data class RoomCreateInputUiState(
    val title: String = "",
    val password: String = "",
    val thumbnail: Bitmap? = null,
    val timer: String = 25.toString(),
    val shortBreak: String = 5.toString(),
    val longBreak: String = 15.toString(),
    val longBreakInterval: String = 4.toString(),
    val expiredAt: Date = get30DaysAfterDate(),
    val onTitleChange: (String) -> Unit,
    val onPasswordChange: (String) -> Unit,
    val onThumbnailChange: (Bitmap) -> Unit,
    val onTimerChange: (String) -> Unit,
    val onShortBreakChange: (String) -> Unit,
    val onLongBreakChange: (String) -> Unit,
    val onLongBreakIntervalChange: (String) -> Unit,
    val onExpiredDateChange: (Date) -> Unit,
    val onCreateClick: () -> Unit
) {
    val isTitleLengthValid: Boolean
        get() = title.isNotEmpty() && title.length <= RoomConstants.MaxTitleLength

    val isPasswordLengthValid: Boolean
        get() = password.length <= RoomConstants.MaxPasswordLength

    val isTimerValid: Boolean
        get() {
            val length = timer.toIntOrNull() ?: return false
            return RoomConstants.TimerLengthRange.contains(length)
        }

    val isShortBreakValid: Boolean
        get() {
            val length = shortBreak.toIntOrNull() ?: return false
            return RoomConstants.ShortBreakLengthRage.contains(length)
        }

    val isLongBreakValid: Boolean
        get() {
            val length = longBreak.toIntOrNull() ?: return false
            return RoomConstants.LongBreakLengthRange.contains(length)
        }

    val isLongBreakIntervalValid: Boolean
        get() {
            val length = longBreakInterval.toIntOrNull() ?: return false
            return RoomConstants.LongBreakIntervalRange.contains(length)
        }

    val canSave: Boolean
        get() {
            return isTitleLengthValid &&
                isPasswordLengthValid &&
                isTimerValid &&
                isShortBreakValid &&
                isLongBreakValid &&
                isLongBreakIntervalValid
        }
}

private fun get30DaysAfterDate(): Date {
    val millis = currentTimeMillis() + TimeUnit.DAYS.toMillis(30)
    // 해당 날짜에서 시간이 0시 0분인 날짜를 반환한다.
    return Date(millis - (millis % (1000 * 60 * 60 * 24)))
}
