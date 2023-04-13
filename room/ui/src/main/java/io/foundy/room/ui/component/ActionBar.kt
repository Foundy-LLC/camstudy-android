package io.foundy.room.ui.component

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
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.room.domain.PomodoroTimerState
import io.foundy.room.domain.WebRtcServerTimeZone
import io.foundy.room.ui.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Composable
fun ActionBar(
    timerState: PomodoroTimerState,
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
        color = CamstudyTheme.colorScheme.systemUi09
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PomodoroTimer(
                state = timerState,
                pomodoroTimerEventDate = timerEventDate,
                onStartClick = onStartTimerClick,
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onFlipCamera,
                    enabled = enabledLocalVideo,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    CamstudyIcon(
                        modifier = Modifier.size(32.dp),
                        icon = CamstudyIcons.FlipCamera,
                        contentDescription = stringResource(R.string.switch_video)
                    )
                }
                ToggleIconButton(
                    enabled = enabledLocalVideo,
                    enabledIcon = CamstudyIcons.VideoCam,
                    disabledIcon = CamstudyIcons.VideoCamOff,
                    onClick = onToggleVideo
                )
                ToggleIconButton(
                    enabled = enabledLocalHeadset,
                    enabledIcon = CamstudyIcons.Headset,
                    disabledIcon = CamstudyIcons.HeadsetOff,
                    onClick = onToggleHeadset
                )
                ToggleIconButton(
                    enabled = enabledLocalAudio,
                    enabledIcon = CamstudyIcons.Mic,
                    disabledIcon = CamstudyIcons.MicOff,
                    onClick = onToggleAudio
                )
            }
        }
    }
}

@Composable
private fun PomodoroTimer(
    state: PomodoroTimerState,
    pomodoroTimerEventDate: LocalDateTime?,
    onStartClick: () -> Unit
) {
    var elapsedTimeText by remember { mutableStateOf(pomodoroTimerEventDate.elapsedTimeText) }
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
                elapsedTimeText = pomodoroTimerEventDate.elapsedTimeText
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

                CamstudyIcon(
                    modifier = Modifier
                        .size(iconWidth, height = iconHeight)
                        .clickable(
                            enabled = enabled,
                            onClick = {
                                onStartClick()
                                // Prevent multiple clicking
                                enabled = false
                                coroutineScope.launch {
                                    delay(500)
                                    enabled = true
                                }
                            },
                            interactionSource = startButtonInteractionSource,
                            indication = null
                        ),
                    icon = if (startButtonPressed) {
                        CamstudyIcons.StartTimerPressed
                    } else {
                        CamstudyIcons.StartTimer
                    },
                    tint = Color.Unspecified,
                    contentDescription = stringResource(R.string.start_timer)
                )
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

private val LocalDateTime?.elapsedTimeText: String
    get() {
        if (this == null) {
            return "00:00"
        }
        val currentTime = Clock.System.now().toLocalDateTime(WebRtcServerTimeZone)
        val instantDiff = currentTime.toInstant(WebRtcServerTimeZone) -
            this.toInstant(WebRtcServerTimeZone)
        val diffWholeSeconds = instantDiff.inWholeSeconds
        val minutes = diffWholeSeconds / 60
        val seconds = diffWholeSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

@Preview(widthDp = 400)
@Composable
private fun ActionBarPreview() {
    var state by remember { mutableStateOf(PomodoroTimerState.STOPPED) }
    CamstudyTheme {
        ActionBar(
            timerState = state,
            timerEventDate = null,
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
