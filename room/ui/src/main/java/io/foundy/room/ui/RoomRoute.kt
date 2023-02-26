package io.foundy.room.ui

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.room.ui.media.LocalMediaManager
import io.foundy.room.ui.media.MediaManager
import io.foundy.room.ui.media.rememberMediaManager
import io.foundy.room.ui.screen.PermissionRequestScreen
import io.foundy.room.ui.screen.StudyRoomScreen
import io.foundy.room.ui.screen.WaitingRoomScreen
import io.foundy.room.ui.viewmodel.RoomSideEffect
import io.foundy.room.ui.viewmodel.RoomUiState
import io.foundy.room.ui.viewmodel.RoomViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun RoomRoute(
    id: String,
    navigator: DestinationsNavigator,
) {
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    if (permissionsState.allPermissionsGranted) {
        RoomContent(id = id, navigator = navigator)
    } else {
        PermissionRequestScreen(
            shouldShowRationale = permissionsState.shouldShowRationale,
            onRequestClick = permissionsState::launchMultiplePermissionRequest
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomContent(
    id: String,
    navigator: DestinationsNavigator,
    mediaManager: MediaManager = rememberMediaManager(),
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    viewModel.collectSideEffect {
        when (it) {
            is RoomSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.content ?: context.getString(it.defaultContentRes)
                )
            }
        }
    }

    LaunchedEffect(id) {
        viewModel.connect(id)
    }

    LaunchedEffect(Unit) {
        mediaManager.onSessionScreenReady()
    }

    BackHandler {
        // TODO: 사용자에게 한 번 더 확인하기
        mediaManager.disconnect()
        navigator.popBackStack()
    }

    CompositionLocalProvider(LocalMediaManager provides mediaManager) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when (uiState) {
                    is RoomUiState.WaitingRoom -> WaitingRoomScreen(
                        roomTitle = id,
                        uiState = uiState
                    )
                    is RoomUiState.StudyRoom -> StudyRoomScreen(uiState = uiState)
                }
            }
        }
    }
}
