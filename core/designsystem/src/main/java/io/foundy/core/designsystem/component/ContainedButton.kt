package io.foundy.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun ContainedButton(
    modifier: Modifier = Modifier,
    label: String,
    shape: Shape = RoundedCornerShape(8.dp),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colorScheme = CamstudyTheme.colorScheme

    Button(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primaryPress,
            contentColor = colorScheme.systemBackground,
            disabledContainerColor = colorScheme.systemUi02,
            disabledContentColor = colorScheme.systemUi05
        ),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {
        Text(
            text = label,
            style = CamstudyTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
private fun EnabledContainedButtonPreview() {
    ContainedButton(label = "Enabled", enabled = true, onClick = {})
}

@Preview
@Composable
private fun DisabledContainedButtonPreview() {
    ContainedButton(label = "Enabled", enabled = false, onClick = {})
}
