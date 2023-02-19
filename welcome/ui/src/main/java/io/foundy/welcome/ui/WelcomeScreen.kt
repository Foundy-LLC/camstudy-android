package io.foundy.welcome.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun WelcomeRoute(onReplaceToHome: () -> Unit) {
    WelcomeScreen(
        onReplaceToHome = onReplaceToHome
    )
}

@Composable
fun WelcomeScreen(onReplaceToHome: () -> Unit) {

    Column {
        Text("환영화면")
        TextButton(onClick = onReplaceToHome) {
            Text(text = "홈으로")
        }
    }
}
