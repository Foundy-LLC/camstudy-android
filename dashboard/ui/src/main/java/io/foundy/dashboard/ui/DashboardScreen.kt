package io.foundy.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.dashboard.ui.component.Header

@Composable
fun DashboardRoute() {
    DashboardScreen()
}

@Composable
fun DashboardScreen() {
    LazyColumn(
        modifier = Modifier.background(color = CamstudyTheme.colorScheme.systemUi03)
    ) {
        item {
            // TODO: 실제 데이터 전달하기
            Header(
                weeklyStudyMinutes = 2213,
                weeklyRanking = 3,
                growingCrop = null,
                onCropTileClick = { /* TODO: 구현하기 */ }
            )
        }
        dividePadding()
    }
}

private fun LazyListScope.dividePadding() {
    item {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(widthDp = 360)
@Composable
private fun DashboardScreenPreview() {
    CamstudyTheme {
        DashboardScreen()
    }
}
