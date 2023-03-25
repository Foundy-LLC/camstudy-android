package io.foundy.room.ui.component

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.room.domain.PomodoroTimerProperty
import io.foundy.room.ui.R

private val TimerLengthMinutesRange = IntRange(20, 50)
private val ShortBreakMinutesRange = IntRange(3, 10)
private val LongBreakMinutesRange = IntRange(10, 30)
private val LongIntervalRange = IntRange(2, 6)

@Composable
fun rememberPomodoroTimerEditBottomSheetState(
    initTimerProperty: PomodoroTimerProperty
): PomodoroTimerEditBottomSheetState {
    val context = LocalContext.current
    return remember {
        PomodoroTimerEditBottomSheetState(
            context = context,
            initTimerProperty = initTimerProperty
        )
    }
}

@Stable
class PomodoroTimerEditBottomSheetState(
    private val context: Context,
    initTimerProperty: PomodoroTimerProperty
) {
    var timerProperty by mutableStateOf(initTimerProperty)
        private set

    var didUpdate by mutableStateOf(false)

    val timerLengthErrorMessage: String?
        get() {
            if (!TimerLengthMinutesRange.contains(timerProperty.timerLengthMinutes)) {
                return context.getString(
                    R.string.timer_length_error,
                    TimerLengthMinutesRange.first,
                    TimerLengthMinutesRange.last
                )
            }
            return null
        }

    val shortBreakLengthErrorMessage: String?
        get() {
            if (!ShortBreakMinutesRange.contains(timerProperty.shortBreakMinutes)) {
                return context.getString(
                    R.string.short_break_length_error,
                    ShortBreakMinutesRange.first,
                    ShortBreakMinutesRange.last
                )
            }
            return null
        }

    val longBreakLengthErrorMessage: String?
        get() {
            if (!LongBreakMinutesRange.contains(timerProperty.longBreakMinutes)) {
                return context.getString(
                    R.string.long_break_length_error,
                    LongBreakMinutesRange.first,
                    LongBreakMinutesRange.last
                )
            }
            return null
        }

    val longIntervalErrorMessageRes: String?
        get() {
            if (!LongIntervalRange.contains(timerProperty.longBreakInterval)) {
                return context.getString(
                    R.string.long_break_interval_error,
                    LongIntervalRange.first,
                    LongIntervalRange.last
                )
            }
            return null
        }

    val canSave: Boolean
        get() {
            return timerLengthErrorMessage == null &&
                shortBreakLengthErrorMessage == null &&
                longBreakLengthErrorMessage == null &&
                longIntervalErrorMessageRes == null &&
                didUpdate
        }

    fun updateTimerProperty(
        timerLengthMinutes: Int? = null,
        shortBreakMinutes: Int? = null,
        longBreakMinutes: Int? = null,
        longBreakInterval: Int? = null
    ) {
        didUpdate = true
        timerProperty = timerProperty.copy(
            timerLengthMinutes = timerLengthMinutes ?: timerProperty.timerLengthMinutes,
            shortBreakMinutes = shortBreakMinutes ?: timerProperty.shortBreakMinutes,
            longBreakMinutes = longBreakMinutes ?: timerProperty.longBreakMinutes,
            longBreakInterval = longBreakInterval ?: timerProperty.longBreakInterval
        )
    }
}

@Composable
fun PomodoroTimerEditBottomSheet(
    state: PomodoroTimerEditBottomSheetState,
    onSaveClick: (PomodoroTimerProperty) -> Unit,
    onDismiss: () -> Unit
) {
    val timerProperty = state.timerProperty
    val numberKeyboardOption = KeyboardOptions(keyboardType = KeyboardType.Number)
    var showRecheckDialog by remember { mutableStateOf(false) }

    if (state.didUpdate && showRecheckDialog) {
        AlertDialog(
            onDismissRequest = { showRecheckDialog = false },
            text = {
                Text(text = stringResource(R.string.are_you_sure_do_not_save_timer))
            },
            dismissButton = {
                TextButton(onClick = { showRecheckDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.quit))
                }
            }
        )
    }

    // TODO: 키보드가 안보임... https://github.com/holixfactory/bottomsheetdialog-compose/issues/17
    BottomSheetDialog(
        onDismissRequest = {
            if (state.didUpdate) {
                showRecheckDialog = true
            } else {
                onDismiss()
            }
        }
    ) {
        Column {
            Row {
                Text(stringResource(R.string.study_timer_length))
                CamstudyTextField(
                    value = timerProperty.timerLengthMinutes.toString(),
                    onValueChange = {
                        val minutes = it.toIntOrNull() ?: 0
                        state.updateTimerProperty(timerLengthMinutes = minutes)
                    },
                    keyboardOptions = numberKeyboardOption,
                    isError = state.timerLengthErrorMessage != null,
                    supportingText = {
                        state.timerLengthErrorMessage?.let { Text(text = it) }
                    }
                )
            }
            Row {
                Text(stringResource(R.string.short_break_length))
                CamstudyTextField(
                    value = timerProperty.shortBreakMinutes.toString(),
                    onValueChange = {
                        val minutes = it.toIntOrNull() ?: 0
                        state.updateTimerProperty(shortBreakMinutes = minutes)
                    },
                    keyboardOptions = numberKeyboardOption,
                    isError = state.shortBreakLengthErrorMessage != null,
                    supportingText = {
                        state.shortBreakLengthErrorMessage?.let { Text(text = it) }
                    }
                )
            }
            Row {
                Text(stringResource(R.string.long_break_length))
                CamstudyTextField(
                    value = timerProperty.longBreakMinutes.toString(),
                    onValueChange = {
                        val minutes = it.toIntOrNull() ?: 0
                        state.updateTimerProperty(longBreakMinutes = minutes)
                    },
                    keyboardOptions = numberKeyboardOption,
                    isError = state.longBreakLengthErrorMessage != null,
                    supportingText = {
                        state.longBreakLengthErrorMessage?.let { Text(text = it) }
                    }
                )
            }
            Row {
                Text(stringResource(R.string.long_break_interval))
                CamstudyTextField(
                    value = timerProperty.longBreakInterval.toString(),
                    onValueChange = {
                        val minutes = it.toIntOrNull() ?: 0
                        state.updateTimerProperty(longBreakInterval = minutes)
                    },
                    keyboardOptions = numberKeyboardOption,
                    isError = state.longIntervalErrorMessageRes != null,
                    supportingText = {
                        state.longIntervalErrorMessageRes?.let { Text(text = it) }
                    }
                )
            }
            Row {
                Button(
                    onClick = { onSaveClick(state.timerProperty) },
                    enabled = state.canSave
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}
