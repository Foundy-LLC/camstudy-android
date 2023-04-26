package io.foundy.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
internal fun RawButton(
    modifier: Modifier = Modifier,
    label: String,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = CamstudyTheme.colorScheme.primary,
        contentColor = CamstudyTheme.colorScheme.systemBackground,
        disabledContainerColor = CamstudyTheme.colorScheme.systemUi02,
        disabledContentColor = CamstudyTheme.colorScheme.systemUi05
    ),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        enabled = enabled,
        colors = colors,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {
        Text(
            text = label,
            style = CamstudyTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun CamstudyContainedButton(
    modifier: Modifier = Modifier,
    label: String,
    shape: Shape = RoundedCornerShape(8.dp),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colorScheme = CamstudyTheme.colorScheme
    RawButton(
        modifier = modifier,
        onClick = onClick,
        label = label,
        shape = shape,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.systemBackground,
            disabledContainerColor = colorScheme.systemUi02,
            disabledContentColor = colorScheme.systemUi05
        ),
    )
}

@Composable
fun CamstudyTextButton(
    modifier: Modifier = Modifier,
    label: String,
    shape: Shape = RoundedCornerShape(8.dp),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colorScheme = CamstudyTheme.colorScheme
    RawButton(
        modifier = modifier,
        onClick = onClick,
        label = label,
        shape = shape,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = colorScheme.primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colorScheme.systemUi04
        ),
    )
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
