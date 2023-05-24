package io.foundy.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

private val ThumbSize = DpSize(width = 32.dp, height = 32.dp)
private val TrackHeight = 12.dp
private val ThumbPressedElevation = 10.dp
private val ThumbDefaultElevation = 6.dp
private val StateLayerSize = 40.0.dp

private object CamstudySliderDefaults {

    @Composable
    fun Thumb(
        interactionSource: MutableInteractionSource,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        thumbSize: DpSize = ThumbSize
    ) {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        val elevation = if (interactions.isNotEmpty()) {
            ThumbPressedElevation
        } else {
            ThumbDefaultElevation
        }
        val shape = RoundedCornerShape(100.dp)

        Spacer(
            modifier
                .size(thumbSize)
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(
                        bounded = false,
                        radius = StateLayerSize / 2
                    )
                )
                .hoverable(interactionSource = interactionSource)
                .shadow(if (enabled) elevation else 0.dp, shape, clip = false)
                .background(CamstudyTheme.colorScheme.systemBackground, shape)
        )
    }

    @Composable
    fun Track(
        sliderPositions: SliderPositions,
        modifier: Modifier = Modifier,
    ) {
        val inactiveTrackColor = CamstudyTheme.colorScheme.systemUi02
        val activeTrackColor = CamstudyTheme.colorScheme.primary
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(TrackHeight)
        ) {
            val isRtl = layoutDirection == LayoutDirection.Rtl
            val sliderLeft = Offset(0f, center.y)
            val sliderRight = Offset(size.width, center.y)
            val sliderStart = if (isRtl) sliderRight else sliderLeft
            val sliderEnd = if (isRtl) sliderLeft else sliderRight
            val trackStrokeWidth = TrackHeight.toPx()
            drawLine(
                inactiveTrackColor,
                sliderStart,
                sliderEnd,
                trackStrokeWidth,
                StrokeCap.Round
            )
            val sliderValueEnd = Offset(
                sliderStart.x +
                    (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.endInclusive,
                center.y
            )

            val sliderValueStart = Offset(
                sliderStart.x +
                    (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.start,
                center.y
            )

            drawLine(
                activeTrackColor,
                sliderValueStart,
                sliderValueEnd,
                trackStrokeWidth,
                StrokeCap.Round
            )
        }
    }
}

@Composable
fun CamstudySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderPositions) -> Unit = {
        CamstudySliderDefaults.Thumb(
            interactionSource = interactionSource,
            enabled = enabled,
            thumbSize = ThumbSize
        )
    },
    track: @Composable (SliderPositions) -> Unit = { sliderPositions ->
        CamstudySliderDefaults.Track(
            sliderPositions = sliderPositions
        )
    },
    /*@IntRange(from = 0)*/
    steps: Int = 0,
    labelGenerator: (Float) -> String = { it.toInt().toString() }
) {
    val stepWidth = (valueRange.endInclusive - valueRange.start) / (steps + 1)
    Column {
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            valueRange = valueRange,
            onValueChangeFinished = onValueChangeFinished,
            interactionSource = interactionSource,
            thumb = thumb,
            track = track,
            steps = steps
        )
        SliderLabelRow(
            modifier = Modifier.padding(horizontal = ThumbSize.width / 2),
        ) {
            repeat(steps + 2) { index ->
                Text(
                    text = labelGenerator(valueRange.start + stepWidth * index),
                    style = CamstudyTheme.typography.labelMedium.copy(
                        color = CamstudyTheme.colorScheme.systemUi06,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
private fun SliderLabelRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        if (placeables.size < 2) {
            error("Should item count is more than 2 or equals.")
        }
        val remainWidth = constraints.maxWidth
        val widthStep = remainWidth.toFloat() / (placeables.size - 1)

        layout(constraints.maxWidth, placeables.maxOf { it.height }) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(x = (widthStep * index - placeable.width / 2).toInt(), y = 0)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CamstudySliderPreview() {
    CamstudyTheme {
        CamstudySlider(
            value = 3f,
            onValueChange = {},
            valueRange = 1f..10f,
            steps = 8,
            labelGenerator = { it.toInt().toString() }
        )
    }
}
