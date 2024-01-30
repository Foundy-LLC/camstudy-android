package io.foundy.feature.auth.ui

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.feature.auth.ui.componenet.GoogleLoginButton
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

interface LoginNavigator {
    fun replaceToHome()
    fun replaceToWelcome()
}

@Destination(style = DestinationStyle.Runtime::class)
@Composable
fun LoginRoute(
    navigator: LoginNavigator,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val signInWithGoogleLauncher = rememberSignInWithGoogleLauncher(
        onCancelled = { viewModel.setInProgressGoogleSignIn(false) },
        onFailure = {
            viewModel.setInProgressGoogleSignIn(false)
            val message = context.getString(R.string.failed_to_sign_in, it.statusCode)
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    )

    viewModel.collectSideEffect {
        when (it) {
            LoginSideEffect.NavigateToHome -> navigator.replaceToHome()
            LoginSideEffect.NavigateToWelcome -> navigator.replaceToWelcome()
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
            viewModel.setInProgressGoogleSignIn(true)
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(36.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CamstudyIcon(
                icon = CamstudyIcons.LoginTitle,
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(16.dp))
            CamstudyText(
                text = "친환경(?) 온라인 화상 스터디 플랫폼",
                style = CamstudyTheme.typography.titleMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi05,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(36.dp))
            GoogleLoginButton(
                showProgressIndicator = uiState.inProgressGoogleSignIn,
                enabled = !uiState.inProgressGoogleSignIn,
                onClick = onGoogleLoginClick
            )
        }
    }
}

@Composable
private fun rememberSignInWithGoogleLauncher(
    onCancelled: () -> Unit,
    onFailure: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val startActivityForResult = ActivityResultContracts.StartActivityForResult()
    return rememberLauncherForActivityResult(startActivityForResult) remember@{ result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful.not()) {
            onCancelled()
            return@remember
        }
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            Firebase.auth.signInWithCredential(credential)
        } catch (e: ApiException) {
            Log.e("rememberSignInWithGoogleLauncher", e.toString())
            onFailure(e)
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        LoginUiState(),
        snackbarHostState = SnackbarHostState(),
        onGoogleLoginClick = {}
    )
}
