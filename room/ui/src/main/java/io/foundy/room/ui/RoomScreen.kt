package io.foundy.room.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.room.data.model.WaitingRoomData
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun RoomRoute(
    id: String,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value

    LaunchedEffect(id) {
        viewModel.connect(id)
    }

    when (uiState) {
        RoomUiState.Connecting -> ConnectingContent()
        is RoomUiState.FailedToConnect -> FailedToConnectContent()
        is RoomUiState.WaitingRoom -> WaitingRoomContent(uiState.data)
    }
}

@Composable
fun ConnectingContent() {
    Text(text = "Loading...")
}

@Composable
fun FailedToConnectContent() {
    Text(text = "서버 연결 실패!")
}

@Composable
fun WaitingRoomContent(data: WaitingRoomData) {
    Text(text = data.toString())
}
