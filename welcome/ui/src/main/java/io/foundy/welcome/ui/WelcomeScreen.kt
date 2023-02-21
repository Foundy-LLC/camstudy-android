package io.foundy.welcome.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.foundy.core.common.util.toBitmap
import io.foundy.core.designsystem.component.CamstudyTextField
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

interface WelcomeNavigator {
    fun replaceToHome()
}

@Destination(style = DestinationStyle.Runtime::class)
@Composable
fun WelcomeRoute(
    viewModel: WelcomeViewModel = hiltViewModel(),
    navigator: WelcomeNavigator
) {
    val uiState = viewModel.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    viewModel.collectSideEffect {
        when (it) {
            WelcomeSideEffect.NavigateToHome -> navigator.replaceToHome()
            is WelcomeSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.content ?: context.getString(it.defaultContentRes)
                )
            }
        }
    }

    WelcomeScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onImageSelected = viewModel::updateSelectedProfileImage,
        onRemoveImage = viewModel::removeSelectedProfileImage,
        onNameChange = viewModel::updateNameInput,
        onIntroduceChange = viewModel::updateIntroduceInput,
        onTagChange = viewModel::updateTagInput,
        onClickAddTag = viewModel::addTag,
        onClickRemoveTag = viewModel::removeTag,
        onLostFocusNameInput = viewModel::updateNameErrorMessage,
        onLostFocusTagInput = viewModel::updateTagErrorMessage,
        onClickDone = viewModel::saveInitInformation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    uiState: WelcomeUiState,
    snackbarHostState: SnackbarHostState,
    onImageSelected: (Bitmap) -> Unit,
    onRemoveImage: () -> Unit,
    onNameChange: (String) -> Unit,
    onIntroduceChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onClickAddTag: () -> Unit,
    onClickRemoveTag: (String) -> Unit,
    onLostFocusNameInput: () -> Unit,
    onLostFocusTagInput: () -> Unit,
    onClickDone: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            Text("환영합니다!")
            Text("초기정보를 입력해주세요.")
            ImageSelector(
                selectedImage = uiState.selectedProfileImage,
                onImageSelected = onImageSelected,
                onRemoveImage = onRemoveImage
            )
            CamstudyTextField(
                value = uiState.nameInput,
                onValueChange = onNameChange,
                placeholder = { Text(text = "홍길동") },
                label = { Text(text = "이름") },
                isError = uiState.nameErrorMessageRes != null,
                supportingText = if (uiState.nameErrorMessageRes != null) {
                    { Text(text = stringResource(id = uiState.nameErrorMessageRes)) }
                } else null,
                onLostFocus = onLostFocusNameInput
            )
            TextField(
                value = uiState.introduceInput,
                onValueChange = onIntroduceChange,
                placeholder = { Text(text = "저를 소개합니다.") },
                label = { Text(text = "자기소개") }
            )
            Row {
                CamstudyTextField(
                    value = uiState.tagInput,
                    enabled = uiState.enableTagInput,
                    onValueChange = onTagChange,
                    placeholder = { Text(text = "공시, 수능, 개발") },
                    label = { Text(text = "관심 태그") },
                    isError = uiState.tagErrorMessageRes != null,
                    supportingText = if (uiState.tagErrorMessageRes != null) {
                        { Text(text = stringResource(id = uiState.tagErrorMessageRes)) }
                    } else null,
                    onLostFocus = onLostFocusTagInput,
                    trailingIcon = {
                        if (uiState.enabledAddTagButton) {
                            TextButton(onClick = onClickAddTag) {
                                Text(text = "추가")
                            }
                        }
                    }
                )
            }
            Row {
                for (tag in uiState.addedTags) {
                    RemovableChip(label = tag, onRemoveClick = { onClickRemoveTag(tag) })
                }
            }
            TextButton(
                onClick = onClickDone,
                enabled = uiState.enabledDoneButton
            ) {
                Text(text = "완료")
            }
        }
    }
}

@Composable
private fun ImageSelector(
    selectedImage: Bitmap?,
    onImageSelected: (Bitmap) -> Unit,
    onRemoveImage: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        val bitmap = uri.toBitmap(context)
        onImageSelected(bitmap)
    }

    if (selectedImage != null) {
        AsyncImage(
            modifier = Modifier.size(120.dp),
            model = selectedImage,
            contentDescription = stringResource(R.string.selected_profile_image),
        )
        IconButton(onClick = onRemoveImage) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.remove_profile_image),
                tint = Color.Red
            )
        }
    }
    TextButton(onClick = { launcher.launch("image/*") }) {
        Text(text = "프로필 이미지 고르기")
    }
}

@Composable
private fun RemovableChip(
    label: String,
    onRemoveClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.padding(8.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                modifier = Modifier.clickable(onClick = onRemoveClick),
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.tag_remove_button),
            )
        }
    }
}

@Composable
@Preview("Empty input")
private fun WelcomeScreenPreview() {
    WelcomeScreen(
        uiState = WelcomeUiState(),
        snackbarHostState = SnackbarHostState(),
        onImageSelected = {},
        onRemoveImage = {},
        onNameChange = {},
        onIntroduceChange = {},
        onTagChange = {},
        onClickAddTag = {},
        onClickRemoveTag = {},
        onLostFocusNameInput = {},
        onLostFocusTagInput = {},
        onClickDone = {}
    )
}

@Composable
@Preview("Tag chips")
private fun WelcomeScreenTagsPreview() {
    WelcomeScreen(
        uiState = WelcomeUiState(
            addedTags = listOf("공시", "개발")
        ),
        snackbarHostState = SnackbarHostState(),
        onImageSelected = {},
        onRemoveImage = {},
        onNameChange = {},
        onIntroduceChange = {},
        onTagChange = {},
        onClickAddTag = {},
        onClickRemoveTag = {},
        onLostFocusNameInput = {},
        onLostFocusTagInput = {},
        onClickDone = {}
    )
}
