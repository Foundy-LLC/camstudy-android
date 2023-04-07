package io.foundy.core.designsystem.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp

val Int.nonScaledSp
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp

val Float.nonScaledSp
    @Composable
    get() = this.toInt().nonScaledSp
