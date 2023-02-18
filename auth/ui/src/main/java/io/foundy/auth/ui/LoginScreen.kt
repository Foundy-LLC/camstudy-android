package io.foundy.auth.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel()
) {

    LoginScreen()
}

@Composable
fun LoginScreen() {
    Text("Login screen")
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
