package io.foundy.ranking.ui

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.designsystem.component.CamstudyText

@Composable
@Destination
fun RankingRoute() {
    RankingScreen()
}

@Composable
fun RankingScreen() {
    CamstudyText(text = "랭킹화면")
}
