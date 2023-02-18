package io.foundy.camstudy.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.foundy.camstudy.navigation.CamstudyNavHost
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun CamstudyApp(
    appState: CamstudyAppState = rememberCamstudyAppState()
) {
    CamstudyTheme {
        CamstudyNavHost(
            navController = appState.navController,
            onNavigateToDestination = appState::navigate,
            onBackClick = appState::onBackClick
        )
    }
}
