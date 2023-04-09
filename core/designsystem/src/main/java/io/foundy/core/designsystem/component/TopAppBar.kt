package io.foundy.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun CamstudyTopAppBar(
    onBackClick: (() -> Unit)? = null,
    title: @Composable () -> Unit
) {
    val componentColor = CamstudyTheme.colorScheme.systemUi09

    Surface(
        color = CamstudyTheme.colorScheme.systemBackground
    ) {
        Box {
            Row(
                modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                onBackClick?.let { onClick ->
                    IconButton(onClick = onClick) {
                        CamstudyIcon(
                            icon = CamstudyIcons.ArrowBack,
                            contentDescription = null,
                            tint = componentColor
                        )
                    }
                    Box(Modifier.width(4.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    ProvideTextStyle(
                        value = CamstudyTheme.typography.titleMedium.copy(
                            color = componentColor,
                            fontWeight = FontWeight.SemiBold
                        ),
                        content = title
                    )
                }
            }
            CamstudyDivider(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CamstudyTopAppBarPreview() {
    CamstudyTheme {
        Scaffold(
            topBar = {
                CamstudyTopAppBar(
                    onBackClick = {},
                    title = { Text(text = "앱바 타이틀") }
                )
            }
        ) {
            Box(Modifier.padding(it))
        }
    }
}
