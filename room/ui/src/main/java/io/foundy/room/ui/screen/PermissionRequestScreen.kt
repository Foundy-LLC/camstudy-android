package io.foundy.room.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionRequestScreen(
    shouldShowRationale: Boolean,
    onRequestClick: () -> Unit
) {
    Scaffold { padding ->
        Column(Modifier.padding(padding)) {
            val helpText = if (shouldShowRationale) {
                "The camera and audio are important for this app. Please grant the permission."
            } else {
                "Camera permission and audio required for this feature to be available. " +
                    "Please grant the permission"
            }
            Text(helpText)
            Button(onClick = onRequestClick) {
                Text("Request permissions")
            }
        }
    }
}
