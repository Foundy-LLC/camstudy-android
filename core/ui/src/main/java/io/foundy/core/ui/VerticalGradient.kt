package io.foundy.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun VerticalGradient(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    alpha: Float = 1.0f
) {
    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(colors = colors),
            alpha = alpha
        )
    )
}
