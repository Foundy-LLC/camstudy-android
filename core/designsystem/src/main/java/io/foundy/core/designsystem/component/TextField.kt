package io.foundy.core.designsystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamstudyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.filledShape,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    onLostFocus: (() -> Unit)? = null
) {
    var previousFocusState: FocusState? by remember { mutableStateOf(null) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.onFocusChanged {
            if (previousFocusState?.isFocused == true && !it.isFocused) {
                onLostFocus?.invoke()
            }
            previousFocusState = it
        },
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamstudyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.filledShape,
    onLostFocus: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val colorScheme = CamstudyTheme.colorScheme
    val typography = CamstudyTheme.typography

    val surfaceColor = colorScheme.systemUi01
    val borderShape = RoundedCornerShape(8.dp)
    val borderColor = when {
        isError -> colorScheme.danger
        isFocused -> colorScheme.primary
        else -> surfaceColor
    }
    val labelColor = when {
        !enabled -> colorScheme.systemUi04
        // TODO: 색상 danger-05로 해야함
        isError -> colorScheme.danger
        else -> colorScheme.systemUi05
    }
    val textColor = when {
        !enabled -> colorScheme.systemUi03
        isError || isFocused -> colorScheme.systemUi08
        else -> colorScheme.systemUi05
    }
    val supportingTextColor = when {
        !enabled -> colorScheme.systemUi03
        // TODO: 색상 danger-05로 해야함
        isError -> colorScheme.danger
        else -> colorScheme.systemUi04
    }

    Column(modifier = modifier.padding(vertical = 4.dp)) {
        label?.let {
            CamstudyText(
                text = it,
                style = typography.labelMedium.copy(color = labelColor)
            )
            Box(modifier = Modifier.height(6.dp))
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(borderShape)
                .border(width = 1.dp, color = borderColor, shape = borderShape),
            color = colorScheme.systemUi01
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp)) {
                val textStyle = typography.titleSmall.copy(color = textColor)
                // TODO: 터치 영역 더 키우기
                BasicTextField(
                    modifier = modifier
                        .focusRequester(FocusRequester())
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = textStyle,
                    cursorBrush = SolidColor(colorScheme.primary),
                )
                if (value.isEmpty() && placeholder != null) {
                    CamstudyText(text = placeholder, style = textStyle)
                }
            }
        }
        supportingText?.let {
            Box(modifier = Modifier.height(4.dp))
            CamstudyText(
                text = it,
                style = typography.labelMedium.copy(color = supportingTextColor)
            )
        }
    }

//    BasicTextField(
//        value = value,
//        onValueChange = onValueChange,
//        modifier = modifier.onFocusChanged {
//            if (previousFocusState?.isFocused == true && !it.isFocused) {
//                onLostFocus?.invoke()
//            }
//            previousFocusState = it
//        },
//        enabled = enabled,
//        readOnly = readOnly,
//        textStyle = textStyle,
//        label = {
//            label?.let {
//                val color = if (isError) colorScheme.error else colorScheme.systemUi05
//                Text(
//                    text = it,
//                    style = typography.labelMedium.copy(color = color)
//                )
//            }
//        },
//        placeholder = {
//            placeholder?.let {
//                val color = when {
//                    !enabled -> colorScheme.systemUi03
//                    isError -> colorScheme.systemUi08
//                    else -> colorScheme.systemUi05
//                }
//                Text(
//                    text = it,
//                    style = typography.titleSmall.copy(color = color)
//                )
//            }
//        },
//        leadingIcon = leadingIcon,
//        trailingIcon = trailingIcon,
//        prefix = prefix,
//        suffix = suffix,
//        supportingText = {
//            supportingText?.let {
//                val color = if (isError) colorScheme.error else colorScheme.systemUi04
//                Text(
//                    text = it,
//                    style = typography.labelMedium.copy(color = color)
//                )
//            }
//        },
//        isError = isError,
//        visualTransformation = visualTransformation,
//        keyboardOptions = keyboardOptions,
//        keyboardActions = keyboardActions,
//        singleLine = singleLine,
//        maxLines = maxLines,
//        minLines = minLines,
//        interactionSource = interactionSource,
//        shape = shape,
//        colors = colors
//    )
}

@Preview(showBackground = true)
@Composable
fun CamstudyTextFieldPreview() {
    CamstudyTheme {
        CamstudyTextField(
            value = "text",
            label = "레이블",
            placeholder = "플레이스 홀더",
            supportingText = "보조/알림 텍스트",
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CamstudyTextFieldPlaceholderPreview() {
    CamstudyTheme {
        CamstudyTextField(
            value = "",
            label = "레이블",
            placeholder = "플레이스 홀더",
            supportingText = "보조/알림 텍스트",
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DisabledCamstudyTextFieldPreview() {
    CamstudyTheme {
        CamstudyTextField(
            value = "text",
            label = "레이블",
            enabled = false,
            placeholder = "플레이스 홀더",
            supportingText = "보조/알림 텍스트",
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorCamstudyTextFieldPreview() {
    CamstudyTheme {
        CamstudyTextField(
            value = "text",
            label = "레이블",
            isError = true,
            placeholder = "플레이스 홀더",
            supportingText = "보조/알림 텍스트",
            onValueChange = {}
        )
    }
}
