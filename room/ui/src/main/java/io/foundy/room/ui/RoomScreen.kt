package io.foundy.room.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun RoomRoute(
    id: String
) {
    Text("공부방 화면! $id")
}
