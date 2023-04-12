package io.foundy.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun CamstudySwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = CamstudyTheme.colorScheme.systemBackground,
        checkedTrackColor = CamstudyTheme.colorScheme.primary,
        uncheckedThumbColor = CamstudyTheme.colorScheme.systemBackground,
        uncheckedTrackColor = CamstudyTheme.colorScheme.systemUi02,
        uncheckedBorderColor = CamstudyTheme.colorScheme.systemUi02
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    // TODO: 디자인된 토글 버튼 요구사항에 맞춰 구현하기
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        thumbContent = thumbContent,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}

@Preview
@Composable
fun CamstudySwitchPreview() {
    CamstudyTheme {
        CamstudySwitch(checked = true, onCheckedChange = {})
    }
}

@Preview
@Composable
fun UncheckedCamstudySwitchPreview() {
    CamstudyTheme {
        CamstudySwitch(checked = false, onCheckedChange = {})
    }
}
