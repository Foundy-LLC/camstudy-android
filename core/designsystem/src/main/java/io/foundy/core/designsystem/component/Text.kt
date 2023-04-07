package io.foundy.core.designsystem.component

import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.designsystem.theme.Typography

enum class CamstudyTextStyle(
    textStyle: TextStyle = TextStyle.Default,
    val lastBaselineToBottom: TextUnit = TextUnit.Unspecified
) {
    Unspecified,
    DisplayLarge(Typography.displayLarge, 15.sp),
    DisplayMedium(Typography.displayMedium, 12.sp),
    DisplaySmall(Typography.displaySmall, 10.sp),
    HeadlineLarge(Typography.headlineLarge, 10.sp),
    HeadlineMedium(Typography.headlineMedium, 9.sp),
    HeadlineSmall(Typography.headlineSmall, 7.sp),
    TitleLarge(Typography.titleLarge, 7.sp),
    TitleMedium(Typography.titleMedium, 5.sp),
    TitleSmall(Typography.titleSmall, 4.sp),
    LabelMedium(Typography.labelMedium, 4.sp);

    val firstBaselineToTop: TextUnit = TextUnit(
        textStyle.lineHeight.value - lastBaselineToBottom.value, TextUnitType.Sp
    )
}

@Composable
fun camstudyTextStyle(style: TextStyle = LocalTextStyle.current): CamstudyTextStyle {
    val equals: ((TextStyle, TextStyle) -> Boolean) = { style1, style2 ->
        style1.fontFamily == style2.fontFamily &&
            style1.fontSize == style2.fontSize &&
            style1.lineHeight == style2.lineHeight
    }

    return when {
        equals(CamstudyTheme.typography.displayLarge, style) -> CamstudyTextStyle.DisplayLarge
        equals(CamstudyTheme.typography.displayMedium, style) -> CamstudyTextStyle.DisplayMedium
        equals(CamstudyTheme.typography.displaySmall, style) -> CamstudyTextStyle.DisplaySmall
        equals(CamstudyTheme.typography.headlineLarge, style) -> CamstudyTextStyle.HeadlineLarge
        equals(CamstudyTheme.typography.headlineMedium, style) -> CamstudyTextStyle.HeadlineMedium
        equals(CamstudyTheme.typography.headlineSmall, style) -> CamstudyTextStyle.HeadlineSmall
        equals(CamstudyTheme.typography.titleLarge, style) -> CamstudyTextStyle.TitleLarge
        equals(CamstudyTheme.typography.titleMedium, style) -> CamstudyTextStyle.TitleMedium
        equals(CamstudyTheme.typography.titleSmall, style) -> CamstudyTextStyle.TitleSmall
        equals(CamstudyTheme.typography.labelMedium, style) -> CamstudyTextStyle.LabelMedium
        else -> CamstudyTextStyle.Unspecified
    }
}

@Composable
fun CamstudyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val textStyle: CamstudyTextStyle = camstudyTextStyle(style = style)
    val baselineModifier = if (textStyle != CamstudyTextStyle.Unspecified) {
        Modifier.paddingFromBaseline(
            top = textStyle.firstBaselineToTop,
            bottom = textStyle.lastBaselineToBottom
        )
    } else {
        Modifier
    }

    Text(
        text = text,
        modifier = modifier.then(baselineModifier),
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        style = style
    )
}
