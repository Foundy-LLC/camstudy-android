package io.foundy.room_list.ui.create

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.core.designsystem.component.BottomContainedButton
import io.foundy.core.designsystem.component.BottomContainedButtonBoxHeight
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.component.SelectableTile
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.constant.RoomConstants
import io.foundy.core.ui.TagInputTextField
import io.foundy.room.ui.RoomActivity
import io.foundy.room_list.ui.R
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun RoomCreateScreen(
    navigator: DestinationsNavigator,
    viewModel: RoomCreateViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showRecheckDialog by remember { mutableStateOf(false) }

    viewModel.collectSideEffect {
        when (it) {
            is RoomCreateSideEffect.ErrorMessage -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.message.content ?: context.getString(it.message.defaultRes)
                )
            }
            is RoomCreateSideEffect.SuccessToCreate -> {
                navigator.navigateUp()
                val intent = RoomActivity.getIntent(context, it.createdRoom)
                context.startActivity(intent)
            }
        }
    }

    if (showRecheckDialog) {
        CamstudyDialog(
            content = stringResource(R.string.room_create_recheck_dialog_content),
            onCancel = { showRecheckDialog = false },
            onConfirm = { navigator.navigateUp() },
            confirmText = stringResource(R.string.room_create_recheck_dialog_confirm)
        )
    }

    BackHandler {
        if (uiState.isInCreating) {
            return@BackHandler
        }
        showRecheckDialog = !showRecheckDialog
    }

    RoomCreateContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = { showRecheckDialog = true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomCreateContent(
    uiState: RoomCreateUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CamstudyTopAppBar(
                title = {
                    CamstudyText(text = stringResource(id = R.string.room_create_app_bar_title))
                },
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = CamstudyTheme.colorScheme.systemBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Title()
                CamstudyTextField(
                    value = uiState.title,
                    onValueChange = uiState.onTitleChange,
                    label = stringResource(R.string.room_title),
                    singleLine = true,
                    isError = uiState.isExceedTitleLength,
                    placeholder = stringResource(R.string.room_create_title_placeholder),
                    supportingText = uiState.titleSupportingTextRes,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                TagInputTextField(
                    value = uiState.tag,
                    onValueChange = uiState.onTagChange,
                    addedTags = uiState.addedTags,
                    recommendedTags = uiState.recommendedTags,
                    onAdd = { tag ->
                        if (uiState.addedTags.size == RoomConstants.MaxTagCount - 1) {
                            focusManager.clearFocus()
                        }
                        uiState.onAddTag(tag)
                    },
                    onRemove = uiState.onRemoveTag,
                    label = stringResource(R.string.room_tag),
                    placeholder = stringResource(R.string.room_tag_placeholder),
                    supportingText = if (uiState.isTagFull) {
                        stringResource(R.string.room_tag_is_done)
                    } else {
                        stringResource(R.string.room_tag_supporting_text)
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                CamstudyDivider()
                Spacer(modifier = Modifier.height(20.dp))
                SelectableTile(
                    title = stringResource(R.string.room_private_tile_title),
                    subtitle = stringResource(R.string.room_private_tile_subtitle),
                    checked = uiState.password != null,
                    onCheckedChange = { isPrivate ->
                        uiState.onPasswordChange(if (isPrivate) "" else null)
                    }
                )
                AnimatedVisibility (uiState.password != null) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        CamstudyTextField(
                            value = uiState.password ?: "",
                            isError = uiState.isExceedPasswordLength,
                            onValueChange = uiState.onPasswordChange,
                            placeholder = stringResource(R.string.room_password_placeholder),
                            supportingText = uiState.passwordSupportingTextRes,
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Go,
                                keyboardType = KeyboardType.Password
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    if (uiState.canCreate) {
                                        uiState.onCreateClick()
                                    }
                                }
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(BottomContainedButtonBoxHeight + 40.dp))
            }
            BottomContainedButton(
                enabled = uiState.canCreate,
                label = if (uiState.isInCreating) {
                    stringResource(R.string.creating)
                } else {
                    stringResource(R.string.create_and_join)
                },
                onClick = uiState.onCreateClick
            )
        }
    }
}

@Composable
private fun Title() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        CamstudyText(
            text = stringResource(R.string.room_create_title),
            style = CamstudyTheme.typography.headlineSmall.copy(
                color = CamstudyTheme.colorScheme.systemUi08,
                fontWeight = FontWeight.SemiBold
            )
        )
        CamstudyText(
            text = stringResource(R.string.room_create_subtitle),
            style = CamstudyTheme.typography.titleMedium.copy(
                color = CamstudyTheme.colorScheme.systemUi06,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Preview
@Composable
private fun RoomCreateContentPreview() {
    CamstudyTheme {
        RoomCreateContent(
            uiState = RoomCreateUiState(
                password = "",
                onPasswordChange = {},
                onThumbnailChange = {},
                onTagChange = {},
                onAddTag = {},
                onRemoveTag = {},
                onTitleChange = {},
                onCreateClick = {}
            ),
            onBackClick = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
