package io.foundy.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

private val DarkColorScheme = darkCamstudyColorScheme()

private val LightColorScheme = lightCamstudyColorScheme()

internal val LocalCamstudyColorScheme = staticCompositionLocalOf { lightCamstudyColorScheme() }

internal val LocalCamstudyTypography = staticCompositionLocalOf { Typography() }

object CamstudyTheme {

    val colorScheme: CamstudyColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalCamstudyColorScheme.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalCamstudyTypography.current
}

@Composable
fun CamstudyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    CompositionLocalProvider(
        LocalCamstudyColorScheme provides colorScheme,
        LocalCamstudyTypography provides Typography
    ) {
        MaterialTheme(
            typography = Typography,
            content = content,
        )
    }
}
