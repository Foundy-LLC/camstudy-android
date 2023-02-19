package io.foundy.auth.ui

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    onReplaceToHome: () -> Unit,
    onReplaceToWelcome: () -> Unit
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val signInWithGoogleLauncher = rememberSignInWithGoogleLauncher(
        onFailure = {
            scope.launch {
                snackbarHostState.showSnackbar(
                    it.message ?: context.getString(R.string.unknown_error)
                )
            }
        }
    )

    viewModel.collectSideEffect {
        when (it) {
            LoginSideEffect.NavigateToHome -> onReplaceToHome()
            LoginSideEffect.NavigateToWelcome -> onReplaceToWelcome()
            is LoginSideEffect.Message -> {
                snackbarHostState.showSnackbar(
                    it.message ?: context.getString(it.defaultMessageRes)
                )
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onGoogleLoginClick = {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEB_CLIENT_ID)
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, options)
            signInWithGoogleLauncher.launch(googleSignInClient.signInIntent)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onGoogleLoginClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(32.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text("로그인 화면")
            TextButton(onClick = onGoogleLoginClick) {
                Text(text = "Sign in with Google")
            }
        }
    }
}

@Composable
private fun rememberSignInWithGoogleLauncher(
    onFailure: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            Firebase.auth.signInWithCredential(credential)
        } catch (e: ApiException) {
            onFailure(e)
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        LoginUiState,
        snackbarHostState = SnackbarHostState(),
        onGoogleLoginClick = {}
    )
}
