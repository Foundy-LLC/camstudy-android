package io.foundy.feature.room.ui.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyTooltipBox
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.feature.room.domain.PomodoroTimerProperty
import io.foundy.feature.room.domain.PomodoroTimerState
import io.foundy.feature.room.domain.WebRtcServerTimeZone
import io.foundy.feature.room.ui.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

private val ActionBarColor = Color(0xFF191919)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBar(
    timerState: PomodoroTimerState,
    timerProperty: PomodoroTimerProperty,
    timerEventDate: LocalDateTime?,
    onStartTimerClick: () -> Unit,
    enabledLocalVideo: Boolean,
    enabledLocalAudio: Boolean,
    enabledLocalHeadset: Boolean,
    onToggleVideo: (Boolean) -> Unit,
    onToggleAudio: (Boolean) -> Unit,
    onToggleHeadset: (Boolean) -> Unit,
    onFlipCamera: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ActionBarColor
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PomodoroTimer(
                state = timerState,
                timerProperty = timerProperty,
                pomodoroTimerEventDate = timerEventDate,
                onStartClick = onStartTimerClick,
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                CamstudyTooltipBox(
                    tooltip = {
                        Text(text = stringResource(R.string.flip_camera))
                    }
                ) {
                    IconButton(
                        modifier = Modifier.tooltipAnchor(),
                        onClick = onFlipCamera,
                        enabled = enabledLocalVideo,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        CamstudyIcon(
                            modifier = Modifier.size(32.dp),
                            icon = CamstudyIcons.FlipCamera,
                            contentDescription = stringResource(R.string.flip_camera)
                        )
                    }
                }
                ToggleIconButton(
                    enabled = enabledLocalVideo,
                    tooltipMessage = stringResource(
                        id = if (enabledLocalVideo) {
                            R.string.turn_off_video
                        } else {
                            R.string.turn_on_video
                        }
                    ),
                    enabledIcon = CamstudyIcons.VideoCam,
                    disabledIcon = CamstudyIcons.VideoCamOff,
                    onClick = onToggleVideo
                )
                ToggleIconButton(
                    enabled = enabledLocalHeadset,
                    tooltipMessage = stringResource(
                        id = if (enabledLocalHeadset) {
                            R.string.turn_off_headset
                        } else {
                            R.string.turn_on_headset
                        }
                    ),
                    enabledIcon = CamstudyIcons.Headset,
                    disabledIcon = CamstudyIcons.HeadsetOff,
                    onClick = onToggleHeadset
                )
                ToggleIconButton(
                    enabled = enabledLocalAudio,
                    tooltipMessage = stringResource(
                        id = if (enabledLocalAudio) {
                            R.string.turn_off_mic
                        } else {
                            R.string.turn_on_mic
                        }
                    ),
                    enabledIcon = CamstudyIcons.Mic,
                    disabledIcon = CamstudyIcons.MicOff,
                    onClick = onToggleAudio
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PomodoroTimer(
    state: PomodoroTimerState,
    timerProperty: PomodoroTimerProperty,
    pomodoroTimerEventDate: LocalDateTime?,
    onStartClick: () -> Unit
) {
    var elapsedTimeText by remember(pomodoroTimerEventDate) {
        mutableStateOf(
            pomodoroTimerEventDate.getRemainTimeText(
                timerState = state,
                timerProperty = timerProperty
            )
        )
    }
    val color = when (state) {
        PomodoroTimerState.STOPPED -> Color.White
        PomodoroTimerState.STARTED -> CamstudyTheme.colorScheme.primary
        PomodoroTimerState.SHORT_BREAK -> Color(0xFF35DC8C)
        PomodoroTimerState.LONG_BREAK -> Color(0xFF2A8CFE)
    }
    val shouldShowStartButton = state == PomodoroTimerState.STOPPED
    val startButtonInteractionSource = remember { MutableInteractionSource() }
    val startButtonPressed by startButtonInteractionSource.collectIsPressedAsState()
    val iconHeight = 40.dp
    val iconWidth by animateDpAsState(if (shouldShowStartButton) 48.dp else 24.dp)

    if (state != PomodoroTimerState.STOPPED) {
        LaunchedEffect(pomodoroTimerEventDate) {
            while (true) {
                delay(1_000)
                elapsedTimeText = pomodoroTimerEventDate.getRemainTimeText(
                    timerState = state,
                    timerProperty = timerProperty
                )
            }
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Crossfade(
            targetState = shouldShowStartButton,
            modifier = Modifier.width(iconWidth)
        ) { shouldShowStartButton ->
            if (shouldShowStartButton) {
                val coroutineScope = rememberCoroutineScope()
                var enabled by remember { mutableStateOf(true) }

                CamstudyTooltipBox(
                    tooltip = { Text(text = stringResource(id = R.string.start_timer)) }
                ) {
                    CamstudyIcon(
                        modifier = Modifier
                            .size(iconWidth, height = iconHeight)
                            .clickable(
                                enabled = enabled,
                                onClick = {
                                    // Prevent multiple clicking
                                    enabled = false
                                    onStartClick()
                                    coroutineScope.launch {
                                        delay(500)
                                        enabled = true
                                    }
                                },
                                interactionSource = startButtonInteractionSource,
                                indication = null
                            )
                            .tooltipAnchor(),
                        icon = if (startButtonPressed) {
                            CamstudyIcons.StartTimerPressed
                        } else {
                            CamstudyIcons.StartTimer
                        },
                        tint = Color.Unspecified,
                        contentDescription = stringResource(R.string.start_timer)
                    )
                }
            } else {
                CamstudyIcon(
                    modifier = Modifier.size(iconWidth, height = iconHeight),
                    icon = CamstudyIcons.Timer,
                    contentDescription = null,
                    tint = color
                )
            }
        }
        Box(modifier = Modifier.width(12.dp))
        Text(
            text = elapsedTimeText,
            style = CamstudyTheme.typography.headlineSmall.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

private fun LocalDateTime?.getRemainTimeText(
    timerState: PomodoroTimerState,
    timerProperty: PomodoroTimerProperty
): String {
    if (this == null) {
        return "00:00"
    }
    val durationMinutes = when (timerState) {
        PomodoroTimerState.STOPPED -> 0
        PomodoroTimerState.STARTED -> timerProperty.timerLengthMinutes
        PomodoroTimerState.SHORT_BREAK -> timerProperty.shortBreakMinutes
        PomodoroTimerState.LONG_BREAK -> timerProperty.longBreakMinutes
    }
    val currentTime = Clock.System.now().toLocalDateTime(WebRtcServerTimeZone)
    val targetTime = this.toInstant(WebRtcServerTimeZone) + durationMinutes.minutes
    val remainTime = targetTime - currentTime.toInstant(WebRtcServerTimeZone)
    val remainWholeSeconds = remainTime.inWholeSeconds + 1
    if (remainWholeSeconds <= 0) {
        return "00:00"
    }
    val minutes = remainWholeSeconds / 60
    val seconds = remainWholeSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Preview(widthDp = 320, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ActionBarPreview() {
    var state by remember { mutableStateOf(PomodoroTimerState.STOPPED) }
    CamstudyTheme {
        ActionBar(
            timerState = state,
            timerEventDate = null,
            timerProperty = PomodoroTimerProperty(
                timerLengthMinutes = 25,
                shortBreakMinutes = 5,
                longBreakMinutes = 15,
                longBreakInterval = 4
            ),
            onStartTimerClick = {
                state = PomodoroTimerState.STARTED
            },
            enabledLocalVideo = true,
            enabledLocalAudio = true,
            enabledLocalHeadset = true,
            onToggleVideo = {},
            onToggleAudio = {},
            onToggleHeadset = {},
            onFlipCamera = {}
        )
    }
}

@Preview(widthDp = 400)
@Composable
private fun ActionBarDisabledVideoPreview() {
    var state by remember { mutableStateOf(PomodoroTimerState.STOPPED) }
    CamstudyTheme {
        ActionBar(
            timerState = state,
            timerEventDate = null,
            timerProperty = PomodoroTimerProperty(
                timerLengthMinutes = 25,
                shortBreakMinutes = 5,
                longBreakMinutes = 15,
                longBreakInterval = 4
            ),
            onStartTimerClick = {
                state = PomodoroTimerState.STARTED
            },
            enabledLocalVideo = false,
            enabledLocalAudio = true,
            enabledLocalHeadset = true,
            onToggleVideo = {},
            onToggleAudio = {},
            onToggleHeadset = {},
            onFlipCamera = {}
        )
    }
}

@Preview(widthDp = 400)
@Composable
private fun TimerStartedActionBarPreview() {
    CamstudyTheme {
        ActionBar(
            timerState = PomodoroTimerState.STARTED,
            timerEventDate = null,
            onStartTimerClick = {},
            timerProperty = PomodoroTimerProperty(
                timerLengthMinutes = 25,
                shortBreakMinutes = 5,
                longBreakMinutes = 15,
                longBreakInterval = 4
            ),
            enabledLocalVideo = true,
            enabledLocalAudio = true,
            enabledLocalHeadset = true,
            onToggleVideo = {},
            onToggleAudio = {},
            onToggleHeadset = {},
            onFlipCamera = {}
        )
    }
}

@Preview(widthDp = 400)
@Composable
private fun TimerShortBreakActionBarPreview() {
    CamstudyTheme {
        ActionBar(
            timerState = PomodoroTimerState.SHORT_BREAK,
            timerEventDate = null,
            timerProperty = PomodoroTimerProperty(
                timerLengthMinutes = 25,
                shortBreakMinutes = 5,
                longBreakMinutes = 15,
                longBreakInterval = 4
            ),
            onStartTimerClick = {},
            enabledLocalVideo = true,
            enabledLocalAudio = true,
            enabledLocalHeadset = true,
            onToggleVideo = {},
            onToggleAudio = {},
            onToggleHeadset = {},
            onFlipCamera = {}
        )
    }
}

@Preview(widthDp = 400)
@Composable
private fun TimerLongBreakActionBarPreview() {
    CamstudyTheme {
        ActionBar(
            timerState = PomodoroTimerState.LONG_BREAK,
            timerEventDate = null,
            timerProperty = PomodoroTimerProperty(
                timerLengthMinutes = 25,
                shortBreakMinutes = 5,
                longBreakMinutes = 15,
                longBreakInterval = 4
            ),
            onStartTimerClick = {},
            enabledLocalVideo = true,
            enabledLocalAudio = true,
            enabledLocalHeadset = true,
            onToggleVideo = {},
            onToggleAudio = {},
            onToggleHeadset = {},
            onFlipCamera = {}
        )
    }
}
