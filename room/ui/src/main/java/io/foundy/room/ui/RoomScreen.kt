package io.foundy.room.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.room.data.model.WaitingRoomData
import org.orbitmvi.orbit.compose.collectAsState

@Destination
@Composable
fun RoomRoute(
    id: String,
    navigator: DestinationsNavigator,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value

    LaunchedEffect(id) {
        viewModel.connect(id)
    }

    BackHandler {
        // TODO: 사용자에게 한 번 더 확인하기
        // TODO: 소켓 연결 끊기
        navigator.popBackStack()
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
