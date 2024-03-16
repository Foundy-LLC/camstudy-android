package io.foundy.core.designsystem.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun ButtonDefaults.camstudyTextButtonColors(
    containerColor: Color = Color.Transparent,
    contentColor: Color = CamstudyTheme.colorScheme.primary,
    disabledContainerColor: Color = Color.Transparent,
    disabledContentColor: Color = CamstudyTheme.colorScheme.systemUi04
) = buttonColors(
    containerColor = containerColor,
    contentColor = contentColor,
    disabledContainerColor = disabledContainerColor,
    disabledContentColor = disabledContentColor
)

@Composable
internal fun RawButton(
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = CamstudyTheme.colorScheme.primary,
        contentColor = CamstudyTheme.colorScheme.systemBackground,
        disabledContainerColor = CamstudyTheme.colorScheme.systemUi02,
        disabledContentColor = CamstudyTheme.colorScheme.systemUi05
    ),
    enabled: Boolean = true,
    border: BorderStroke? = null,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        enabled = enabled,
        colors = colors,
        border = border,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leading != null) {
                leading()
                Spacer(modifier = Modifier.width(10.dp))
            }
            ProvideTextStyle(
                value = CamstudyTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            ) {
                content()
            }
        }
    }
}

@Composable
private fun DefaultContentText(text: String, enableLabelSizeAnimation: Boolean = false) {
    Text(
        modifier = if (enableLabelSizeAnimation) {
            Modifier.animateContentSize()
        } else {
            Modifier
        },
        text = text,
    )
}

@Composable
private fun DefaultLeadingIcon(icon: CamstudyIcon) {
    CamstudyIcon(
        modifier = Modifier.size(20.dp),
        icon = icon,
        contentDescription = null,
    )
}

@Composable
fun CamstudyOutlinedButton(
    modifier: Modifier = Modifier,
    label: String,
    leadingIcon: CamstudyIcon? = null,
    enableLabelSizeAnimation: Boolean = false,
    shape: Shape = RoundedCornerShape(8.dp),
    enabled: Boolean = true,
    containerColor: Color = CamstudyTheme.colorScheme.systemBackground,
    contentColor: Color = CamstudyTheme.colorScheme.primary,
    borderColor: Color = if (enabled) {
        CamstudyTheme.colorScheme.primary
    } else {
        CamstudyTheme.colorScheme.systemUi03
    },
    onClick: () -> Unit
) {
    val colorScheme = CamstudyTheme.colorScheme
    RawButton(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        enabled = enabled,
        leading = if (leadingIcon != null) {
            { DefaultLeadingIcon(icon = leadingIcon) }
        } else null,
        border = BorderStroke(width = 1.dp, color = borderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = colorScheme.systemBackground,
            disabledContentColor = colorScheme.systemUi04
        ),
    ) {
        DefaultContentText(text = label, enableLabelSizeAnimation = enableLabelSizeAnimation)
    }
}

@Composable
fun CamstudyContainedButton(
    modifier: Modifier = Modifier,
    label: String,
    leadingIcon: CamstudyIcon? = null,
    enableLabelSizeAnimation: Boolean = false,
    shape: Shape = RoundedCornerShape(8.dp),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colorScheme = CamstudyTheme.colorScheme
    RawButton(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        leading = if (leadingIcon != null) {
            { DefaultLeadingIcon(icon = leadingIcon) }
        } else null,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.systemBackground,
            disabledContainerColor = colorScheme.systemUi02,
            disabledContentColor = colorScheme.systemUi05
        ),
    ) {
        DefaultContentText(text = label, enableLabelSizeAnimation = enableLabelSizeAnimation)
    }
}

@Composable
fun CamstudyTextButton(
    modifier: Modifier = Modifier,
    label: String,
    leadingIcon: CamstudyIcon? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.camstudyTextButtonColors(),
    onClick: () -> Unit
) {
    RawButton(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        leading = if (leadingIcon != null) {
            { DefaultLeadingIcon(icon = leadingIcon) }
        } else null,
        enabled = enabled,
        colors = colors,
    ) {
        DefaultContentText(text = label)
    }
}

@Preview
@Composable
private fun EnabledOutlinedButtonPreview() {
    CamstudyOutlinedButton(label = "Enabled", enabled = true, onClick = {})
}

@Preview
@Composable
private fun DisabledOutlinedButtonPreview() {
    CamstudyOutlinedButton(label = "Enabled", enabled = false, onClick = {})
}

@Preview
@Composable
private fun EnabledContainedButtonPreview() {
    CamstudyContainedButton(label = "Enabled", enabled = true, onClick = {})
}

@Preview
@Composable
private fun DisabledContainedButtonPreview() {
    CamstudyContainedButton(label = "Enabled", enabled = false, onClick = {})
}

@Preview
@Composable
private fun EnabledTextButtonPreview() {
    CamstudyTextButton(label = "Enabled", enabled = true, onClick = {})
}

@Preview
@Composable
private fun DisabledTextButtonPreview() {
    CamstudyTextButton(label = "Enabled", enabled = false, onClick = {})
}
