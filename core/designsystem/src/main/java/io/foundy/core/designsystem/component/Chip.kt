package io.foundy.core.designsystem.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme

// TODO: 디자인에 맞게 padding 수정하기
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamstudyFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colorScheme = CamstudyTheme.colorScheme

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            ProvideTextStyle(value = CamstudyTheme.typography.titleSmall) {
                label()
            }
        },
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(24.dp),
        leadingIcon = {
            if (selected) {
                CamstudyIcon(icon = CamstudyIcons.Done, contentDescription = null)
            }
        },
        border = FilterChipDefaults.filterChipBorder(
            borderColor = colorScheme.primary
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colorScheme.systemBackground,
            labelColor = colorScheme.primary,
            selectedContainerColor = colorScheme.primary,
            selectedLabelColor = colorScheme.systemBackground,
            selectedLeadingIconColor = colorScheme.systemBackground,
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun CamstudyFilterChipPreview() {
    CamstudyTheme {
        CamstudyFilterChip(
            selected = true,
            onClick = {},
            label = { CamstudyText(text = "선택됨") }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnselectedCamstudyFilterChipPreview() {
    CamstudyTheme {
        CamstudyFilterChip(
            selected = false,
            onClick = {},
            label = { CamstudyText(text = "해제됨") }
        )
    }
}
