package io.foundy.camstudy.ui

import androidx.compose.runtime.Composable
import io.foundy.camstudy.navigation.CamstudyNavHost
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun CamstudyApp(
    appState: CamstudyAppState = rememberCamstudyAppState()
) {
    CamstudyTheme {
        CamstudyNavHost(
            navController = appState.navController,
            navigate = appState::navigate,
            popUpAndNavigate = appState::popUpAndNavigate,
            onBackClick = appState::onBackClick,
            enabledTransition = appState.enabledTransition
        )
    }
}
