package io.foundy.room.ui

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
import io.foundy.room.ui.media.LocalMediaManager
import io.foundy.room.ui.media.MediaManager
import io.foundy.room.ui.screen.StudyRoomScreen
import io.foundy.room.ui.screen.WaitingRoomScreen
import io.foundy.room.ui.viewmodel.RoomSideEffect
import io.foundy.room.ui.viewmodel.RoomUiState
import io.foundy.room.ui.viewmodel.RoomViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomContent(
    id: String,
    popBackStack: () -> Unit,
    viewModel: RoomViewModel,
    mediaManager: MediaManager,
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    viewModel.collectSideEffect {
        when (it) {
            is RoomSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.content ?: context.getString(it.defaultContentRes, it.stringResArgs)
                )
            }
            is RoomSideEffect.OnChatMessage -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    "${it.message.authorName}: ${it.message.content}"
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
        // TODO: ??????????????? ??? ??? ??? ????????????
        mediaManager.disconnect()
        popBackStack()
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
                    is RoomUiState.StudyRoom -> StudyRoomScreen(
                        uiState = uiState,
                        onDismissKickedDialog = {
                            popBackStack()
                        }
                    )
                }
            }
        }
    }
}
