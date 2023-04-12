package io.foundy.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun SelectableTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onCheckedChange(!checked) }
            .then(modifier)
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = CamstudyTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = CamstudyTheme.colorScheme.systemUi08
                )
            )
            Text(
                text = subtitle,
                style = CamstudyTheme.typography.labelMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi04
                )
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        CamstudySwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Preview(showBackground = true)
@Composable
fun SelectableTilePreview() {
    CamstudyTheme {
        SelectableTile(
            title = "타이틀입니다",
            subtitle = "여기에 설명을 주로 적습니다.",
            checked = true,
            onCheckedChange = {}
        )
    }
}
