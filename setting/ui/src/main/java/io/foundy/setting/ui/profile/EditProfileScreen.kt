package io.foundy.setting.ui.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import io.foundy.core.common.util.toBitmap
import io.foundy.core.designsystem.component.BottomContainedButton
import io.foundy.core.designsystem.component.CamstudyOutlinedButton
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextButton
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.constant.UserConstants
import io.foundy.core.ui.TagInputTextField
import io.foundy.core.ui.UserProfileImage
import io.foundy.setting.ui.R
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Parcelize
class StringList(
    private val list: List<String>
) : Parcelable, List<String> by list

@Composable
@Destination
fun EditProfileRoute(
    resultNavigator: ResultBackNavigator<Boolean>,
    name: String,
    introduce: String?,
    imageUrl: String?,
    tags: StringList,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.bind(
            name = name,
            introduce = introduce ?: "",
            imageUrl = imageUrl,
            tags = tags
        )
    }

    viewModel.collectSideEffect {
        when (it) {
            is EditProfileSideEffect.ErrorMessage -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.message.content ?: context.getString(it.message.defaultRes)
                )
            }
            EditProfileSideEffect.SuccessToSave -> {
                resultNavigator.navigateBack(result = true)
            }
        }
    }

    // TODO: 편집된 경우 정말 뒤로 갈 것이냐고 물어보기

    EditProfileScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = { resultNavigator.navigateBack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CamstudyTopAppBar(
                title = {
                    CamstudyText(text = stringResource(R.string.edit_profile_title))
                },
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = CamstudyTheme.colorScheme.systemBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                ProfileImage(
                    model = uiState.selectedImage ?: uiState.imageUrl,
                    onSelectImage = uiState.onSelectImage,
                    onUseDefaultClick = uiState.onUseDefaultImageClick
                )
                CamstudyTextField(
                    value = uiState.name,
                    onValueChange = uiState.onNameChange,
                    label = stringResource(R.string.name_title),
                    singleLine = true,
                    isError = uiState.shouldShowNameError,
                    placeholder = stringResource(R.string.name_placeholder),
                    supportingText = uiState.nameSupportingTextRes,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                CamstudyTextField(
                    value = uiState.introduce,
                    onValueChange = uiState.onIntroduceChange,
                    label = stringResource(R.string.introduce),
                    singleLine = false,
                    isError = uiState.shouldShowIntroduceError,
                    placeholder = stringResource(R.string.introduce_placeholder),
                    supportingText = uiState.introduceSupportingTextRes,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                TagInputTextField(
                    value = uiState.tagInput,
                    onValueChange = uiState.onTagChange,
                    addedTags = uiState.tags,
                    recommendedTags = uiState.recommendedTags,
                    onAdd = { tag ->
                        if (uiState.tags.size == UserConstants.MaxTagCount - 1) {
                            focusManager.clearFocus()
                        }
                        uiState.onTagAdd(tag)
                    },
                    onRemove = uiState.onTagRemove,
                    label = stringResource(R.string.tag_label),
                    placeholder = stringResource(R.string.tag_placeholder),
                    supportingText = uiState.tagSupportingTextRes
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
            BottomContainedButton(
                enabled = uiState.canSave,
                label = if (uiState.isInSaving) {
                    stringResource(R.string.in_saving)
                } else {
                    stringResource(R.string.save)
                },
                onClick = uiState.onSaveClick
            )
        }
    }
}

@Composable
private fun ProfileImage(
    model: Any?,
    onSelectImage: (Bitmap?) -> Unit,
    onUseDefaultClick: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        val bitmap = uri.toBitmap(context)
        onSelectImage(bitmap)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        UserProfileImage(
            model = model,
            imageOrContainerSize = 120.dp,
            fallbackIconSize = 80.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        CamstudyOutlinedButton(
            label = stringResource(R.string.change_profile_image),
            onClick = { launcher.launch("image/*") }
        )
        AnimatedVisibility(model != null) {
            Spacer(modifier = Modifier.height(6.dp))
            CamstudyTextButton(
                label = stringResource(R.string.use_default_image),
                onClick = onUseDefaultClick
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
