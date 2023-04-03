package io.foundy.core.designsystem.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color

@Stable
class CamstudyColorScheme(
    systemBackground: Color,
    systemUi01: Color,
    systemUi02: Color,
    systemUi03: Color,
    systemUi04: Color,
    systemUi05: Color,
    systemUi06: Color,
    systemUi07: Color,
    systemUi08: Color,
    systemUi09: Color,
    systemUi10: Color,
    cardUi01: Color,
    cardUi02: Color,
    cardUi03: Color,
    cardUi04: Color,
    primary: Color,
    primaryPress: Color,
    secondary: Color,
    secondaryPress: Color,
    tertiary: Color,
    tertiaryPress: Color,
    success: Color,
    error: Color,
    cancel: Color,
    cancelPress: Color,
    danger: Color,
    dangerPress: Color,
    textEmphasize: Color,
    text01: Color,
    text02: Color,
    text03: Color,
    text04: Color
) {
    var systemBackground by mutableStateOf(systemBackground, structuralEqualityPolicy())
        internal set
    var systemUi01 by mutableStateOf(systemUi01, structuralEqualityPolicy())
        internal set
    var systemUi02 by mutableStateOf(systemUi02, structuralEqualityPolicy())
        internal set
    var systemUi03 by mutableStateOf(systemUi03, structuralEqualityPolicy())
        internal set
    var systemUi04 by mutableStateOf(systemUi04, structuralEqualityPolicy())
        internal set
    var systemUi05 by mutableStateOf(systemUi05, structuralEqualityPolicy())
        internal set
    var systemUi06 by mutableStateOf(systemUi06, structuralEqualityPolicy())
        internal set
    var systemUi07 by mutableStateOf(systemUi07, structuralEqualityPolicy())
        internal set
    var systemUi08 by mutableStateOf(systemUi08, structuralEqualityPolicy())
        internal set
    var systemUi09 by mutableStateOf(systemUi09, structuralEqualityPolicy())
        internal set
    var systemUi10 by mutableStateOf(systemUi10, structuralEqualityPolicy())
        internal set
    var cardUi01 by mutableStateOf(cardUi01, structuralEqualityPolicy())
        internal set
    var cardUi02 by mutableStateOf(cardUi02, structuralEqualityPolicy())
        internal set
    var cardUi03 by mutableStateOf(cardUi03, structuralEqualityPolicy())
        internal set
    var cardUi04 by mutableStateOf(cardUi04, structuralEqualityPolicy())
        internal set
    var primary by mutableStateOf(primary, structuralEqualityPolicy())
        internal set
    var primaryPress by mutableStateOf(primaryPress, structuralEqualityPolicy())
        internal set
    var secondary by mutableStateOf(secondary, structuralEqualityPolicy())
        internal set
    var secondaryPress by mutableStateOf(secondaryPress, structuralEqualityPolicy())
        internal set
    var tertiary by mutableStateOf(tertiary, structuralEqualityPolicy())
        internal set
    var tertiaryPress by mutableStateOf(tertiaryPress, structuralEqualityPolicy())
        internal set
    var success by mutableStateOf(success, structuralEqualityPolicy())
        internal set
    var error by mutableStateOf(error, structuralEqualityPolicy())
        internal set
    var cancel by mutableStateOf(cancel, structuralEqualityPolicy())
        internal set
    var cancelPress by mutableStateOf(cancelPress, structuralEqualityPolicy())
        internal set
    var danger by mutableStateOf(danger, structuralEqualityPolicy())
        internal set
    var dangerPress by mutableStateOf(dangerPress, structuralEqualityPolicy())
        internal set
    var textEmphasize by mutableStateOf(textEmphasize, structuralEqualityPolicy())
        internal set
    var text01 by mutableStateOf(text01, structuralEqualityPolicy())
        internal set
    var text02 by mutableStateOf(text02, structuralEqualityPolicy())
        internal set
    var text03 by mutableStateOf(text03, structuralEqualityPolicy())
        internal set
    var text04 by mutableStateOf(text04, structuralEqualityPolicy())
        internal set
}

fun lightCamstudyColorScheme(): CamstudyColorScheme {
    return CamstudyColorScheme(
        systemBackground = Color(0xFFFFFFFF),
        systemUi01 = Color(0xFFF0F0F0),
        systemUi02 = Color(0xFFE0E0E0),
        systemUi03 = Color(0xFFC1C1C1),
        systemUi04 = Color(0xFFA2A2A2),
        systemUi05 = Color(0xFF838383),
        systemUi06 = Color(0xFF646464),
        systemUi07 = Color(0xFF4B4B4B),
        systemUi08 = Color(0xFF323232),
        systemUi09 = Color(0xFF191919),
        systemUi10 = Color(0xFF0A0A0A),
        cardUi01 = Color(0xFFFFFFFF),
        cardUi02 = Color(0xFFFFFFFF),
        cardUi03 = Color(0xFFFFFFFF),
        cardUi04 = Color(0xFFFFFFFF),
        primary = Color(0xFFFFFFFF),
        primaryPress = Color(0xFFFFC01F),
        secondary = Color(0xFFDDA30E),
        secondaryPress = Color(0xFF323232),
        tertiary = Color(0xFF191919),
        tertiaryPress = Color(0xFFFFFFFF),
        success = Color(0xFFE0E0E0),
        error = Color(0xFF0FBEC9),
        cancel = Color(0xFFDD1A0E),
        cancelPress = Color(0xFFC1C1C1),
        danger = Color(0xFFA2A2A2),
        dangerPress = Color(0xFFDD1A0E),
        textEmphasize = Color(0xFFBB0C01),
        text01 = Color(0xFF000000),
        text02 = Color(0xFF191919),
        text03 = Color(0xFF4B4B4B),
        text04 = Color(0xFF646464),
    )
}

fun darkCamstudyColorScheme(): CamstudyColorScheme {
    return CamstudyColorScheme(
        systemBackground = Color(0xFF171717),
        systemUi01 = Color(0xFF242424),
        systemUi02 = Color(0xFFF0F0F0),
        systemUi03 = Color(0xFF333333),
        systemUi04 = Color(0xFF3D3D3D),
        systemUi05 = Color(0xFF555555),
        systemUi06 = Color(0xFF6F6F6F),
        systemUi07 = Color(0xFF8B8B8B),
        systemUi08 = Color(0xFFA5A5A5),
        systemUi09 = Color(0xFFC1C1C1),
        systemUi10 = Color(0xFFECECEC),
        cardUi01 = Color(0xFFF5F5F5),
        cardUi02 = Color(0xFF242424),
        cardUi03 = Color(0xFFF0F0F0),
        cardUi04 = Color(0xFF333333),
        primary = Color(0xFF3D3D3D),
        primaryPress = Color(0xFF555555),
        secondary = Color(0xFF6F6F6F),
        secondaryPress = Color(0xFFFFDC84),
        tertiary = Color(0xFFFFEAB6),
        tertiaryPress = Color(0xFFECECEC),
        success = Color(0xFFFFFFFF),
        error = Color(0xFF171717),
        cancel = Color(0xFF333333),
        cancelPress = Color(0xFF75F6FF),
        danger = Color(0xFFFF5C51),
        dangerPress = Color(0xFF6F6F6F),
        textEmphasize = Color(0xFF8B8B8B),
        text01 = Color(0xFFFF5C51),
        text02 = Color(0xFFFF8B83),
        text03 = Color(0xFFFFFFFF),
        text04 = Color(0xFFF5F5F5)
    )
}
