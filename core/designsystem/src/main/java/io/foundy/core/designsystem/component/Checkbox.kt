package io.foundy.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun CamstudyCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val colorScheme = CamstudyTheme.colorScheme

    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = CheckboxDefaults.colors(
            checkedColor = colorScheme.primary,
            uncheckedColor = colorScheme.systemUi06,
            checkmarkColor = colorScheme.systemBackground
        )
    )
}
