package io.foundy.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun CamstudyTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    icon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Tab(
        modifier = modifier.height(40.dp),
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        text = {
            Text(
                text = text,
                style = CamstudyTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                )
            )
        },
        selectedContentColor = CamstudyTheme.colorScheme.primary,
        unselectedContentColor = CamstudyTheme.colorScheme.systemUi04,
        icon = icon,
        interactionSource = interactionSource
    )
}

@Composable
fun CamstudyTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = CamstudyTheme.colorScheme.systemBackground,
        indicator = @Composable { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = CamstudyTheme.colorScheme.primary,
                height = 2.dp
            )
        },
        tabs = tabs
    )
}

@Preview(showBackground = true)
@Composable
fun CamstudyTabPreview() {
    CamstudyTheme {
        CamstudyTab(selected = true, onClick = {}, text = "탭 이름")
    }
}

@Preview
@Composable
fun CamstudyTabRow() {
    CamstudyTheme {
        CamstudyTabRow(selectedTabIndex = 0) {
            CamstudyTab(selected = true, onClick = {}, text = "탭 이름1")
            CamstudyTab(selected = false, onClick = {}, text = "탭 이름2")
        }
    }
}
