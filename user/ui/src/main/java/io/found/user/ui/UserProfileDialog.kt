package io.found.user.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.DialogMaxWidth
import io.foundy.core.designsystem.component.DialogMinWidth
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.ui.UserProfileImage
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun UserProfileDialog(
    viewModel: UserProfileDialogViewModel = hiltViewModel(),
    userId: String,
    onCancel: () -> Unit
) {
    val uiState = viewModel.collectAsState().value

    LaunchedEffect(userId) {
        viewModel.fetchUser(id = userId)
    }

    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = Modifier
                .sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth)
                .clip(RoundedCornerShape(16.dp))
                .background(color = CamstudyTheme.colorScheme.cardUi),
        ) {
            UserProfileDialogContent(uiState = uiState)
        }
    }
}

@Composable
private fun UserProfileDialogContent(uiState: UserProfileDialogUiState) {
    when (uiState) {
        is UserProfileDialogUiState.Failure -> CamstudyText(
            text = uiState.message.content ?: stringResource(id = uiState.message.defaultRes)
        )
        UserProfileDialogUiState.Loading -> CircularProgressIndicator()
        is UserProfileDialogUiState.Success -> {
            UserProfileImage(
                imageUrl = uiState.user.profileImage,
                imageOrContainerSize = 100.dp,
                fallbackIconSize = 64.dp,
                cornerShape = RoundedCornerShape(16.dp)
            )
            CamstudyText(text = uiState.user.toString())
        }
    }
}

@Preview
@Composable
private fun UserProfileDialogPreview() {
    CamstudyTheme {
    }
}
