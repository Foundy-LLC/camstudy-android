package io.foundy.room.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyBottomSheetDialog
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudySlider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextButton
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.constant.RoomConstants
import io.foundy.room.domain.PomodoroTimerProperty
import io.foundy.room.ui.R
import kotlin.math.round

private fun IntRange.toFloatRange() = this.first.toFloat()..this.last.toFloat()

@Composable
fun rememberPomodoroTimerEditBottomSheetState(
    initTimerProperty: PomodoroTimerProperty
): PomodoroTimerEditBottomSheetState {
    return remember {
        PomodoroTimerEditBottomSheetState(
            initTimerProperty = initTimerProperty
        )
    }
}

@Stable
class PomodoroTimerEditBottomSheetState(
    private val initTimerProperty: PomodoroTimerProperty
) {
    var timerProperty by mutableStateOf(initTimerProperty)
        private set

    val canSave: Boolean
        get() {
            return initTimerProperty != timerProperty
        }

    fun updateTimerProperty(
        timerLengthMinutes: Int? = null,
        shortBreakMinutes: Int? = null,
        longBreakMinutes: Int? = null,
        longBreakInterval: Int? = null
    ) {
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
    isTimerRunning: Boolean,
    onSaveClick: (PomodoroTimerProperty) -> Unit,
    onDismiss: () -> Unit
) {
    val timerProperty = state.timerProperty
    var showCancelRecheckDialog by remember { mutableStateOf(false) }
    var showStopWarningDialog by remember { mutableStateOf(false) }

    if (showCancelRecheckDialog) {
        CamstudyDialog(
            content = stringResource(R.string.are_you_sure_do_not_save_timer),
            onDismissRequest = { showCancelRecheckDialog = false },
            onConfirm = onDismiss,
            onCancel = { showCancelRecheckDialog = false },
            confirmText = stringResource(R.string.quit)
        )
    }

    if (showStopWarningDialog) {
        CamstudyDialog(
            content = "진행 중인 타이머는 정지 후 저장됩니다.",
            onDismissRequest = { showStopWarningDialog = false },
            onCancel = { showStopWarningDialog = false },
            onConfirm = { onSaveClick(state.timerProperty) },
            confirmText = "정지 및 저장"
        )
    }

    CamstudyBottomSheetDialog(
        onDismissRequest = {
            if (state.canSave) {
                showCancelRecheckDialog = true
            } else {
                onDismiss()
            }
        }
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CamstudyText(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.edit_pomodoro_timer),
                    style = CamstudyTheme.typography.titleLarge.copy(
                        color = CamstudyTheme.colorScheme.systemUi09,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                CamstudyTextButton(
                    label = stringResource(R.string.save),
                    onClick = {
                        if (isTimerRunning) {
                            showStopWarningDialog = true
                        } else {
                            onSaveClick(state.timerProperty)
                        }
                    },
                    enabled = state.canSave
                )
            }
            SliderDivide(
                title = stringResource(R.string.study_timer_length),
                value = timerProperty.timerLengthMinutes,
                onValueChange = {
                    state.updateTimerProperty(timerLengthMinutes = it)
                },
                valueRange = RoomConstants.TimerLengthRange,
                steps = 5
            )
            SliderDivide(
                title = stringResource(R.string.short_break_length),
                value = timerProperty.shortBreakMinutes,
                onValueChange = {
                    state.updateTimerProperty(shortBreakMinutes = it)
                },
                valueRange = RoomConstants.ShortBreakLengthRage,
                steps = 6
            )
            SliderDivide(
                title = stringResource(R.string.long_break_length),
                value = timerProperty.longBreakMinutes,
                onValueChange = {
                    state.updateTimerProperty(longBreakMinutes = it)
                },
                valueRange = RoomConstants.LongBreakLengthRange,
                steps = 3
            )
            SliderDivide(
                title = stringResource(R.string.long_break_interval),
                value = timerProperty.longBreakInterval,
                onValueChange = {
                    state.updateTimerProperty(longBreakInterval = it)
                },
                valueRange = RoomConstants.LongBreakIntervalRange,
                steps = 3
            )
        }
    }
}

@Composable
private fun SliderDivide(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    steps: Int
) {
    Box {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            CamstudyText(
                text = title,
                style = CamstudyTheme.typography.titleSmall.copy(
                    color = CamstudyTheme.colorScheme.systemUi08,
                    fontWeight = FontWeight.SemiBold
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            CamstudySlider(
                value = value.toFloat(),
                onValueChange = { onValueChange(round(it).toInt()) },
                valueRange = valueRange.toFloatRange(),
                steps = steps
            )
        }
        CamstudyDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}
