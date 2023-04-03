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
    cardUi: Color,
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
    var cardUi by mutableStateOf(cardUi, structuralEqualityPolicy())
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
        cardUi = Color(0xFFFFFFFF),
        cardUi01 = Color(0xFFFFFFFF),
        cardUi02 = Color(0xFFFFFFFF),
        cardUi03 = Color(0xFFFFFFFF),
        cardUi04 = Color(0xFFFFFFFF),
        primary = Color(0xFFFFC01F),
        primaryPress = Color(0xFFDDA30E),
        secondary = Color(0xFF323232),
        secondaryPress = Color(0xFF191919),
        tertiary = Color(0xFFFFFFFF),
        tertiaryPress = Color(0xFFE0E0E0),
        success = Color(0xFF0FBEC9),
        error = Color(0xFFDD1A0E),
        cancel = Color(0xFFC1C1C1),
        cancelPress = Color(0xFFA2A2A2),
        danger = Color(0xFFDD1A0E),
        dangerPress = Color(0xFFBB0C01),
        textEmphasize = Color(0xFF000000),
        text01 = Color(0xFF191919),
        text02 = Color(0xFF4B4B4B),
        text03 = Color(0xFF646464),
        text04 = Color(0xFFA2A2A2)
    )
}

fun darkCamstudyColorScheme(): CamstudyColorScheme {
    return CamstudyColorScheme(
        systemBackground = Color(0xFF171717),
        systemUi01 = Color(0xFF242424),
        systemUi02 = Color(0xFF333333),
        systemUi03 = Color(0xFF3D3D3D),
        systemUi04 = Color(0xFF555555),
        systemUi05 = Color(0xFF6F6F6F),
        systemUi06 = Color(0xFF8B8B8B),
        systemUi07 = Color(0xFFA5A5A5),
        systemUi08 = Color(0xFFC1C1C1),
        systemUi09 = Color(0xFFECECEC),
        systemUi10 = Color(0xFFF5F5F5),
        cardUi = Color(0xFF242424),
        cardUi01 = Color(0xFF333333),
        cardUi02 = Color(0xFF3D3D3D),
        cardUi03 = Color(0xFF555555),
        cardUi04 = Color(0xFF6F6F6F),
        primary = Color(0xFFFFDC84),
        primaryPress = Color(0xFFFFEAB6),
        secondary = Color(0xFFECECEC),
        secondaryPress = Color(0xFFFFFFFF),
        tertiary = Color(0xFF171717),
        tertiaryPress = Color(0xFF333333),
        success = Color(0xFF75F6FF),
        error = Color(0xFFFF5C51),
        cancel = Color(0xFF6F6F6F),
        cancelPress = Color(0xFF8B8B8B),
        danger = Color(0xFFFF5C51),
        dangerPress = Color(0xFFFF8B83),
        textEmphasize = Color(0xFFFFFFFF),
        text01 = Color(0xFFF5F5F5),
        text02 = Color(0xFFECECEC),
        text03 = Color(0xFFC1C1C1),
        text04 = Color(0xFFA5A5A5)
    )
}
