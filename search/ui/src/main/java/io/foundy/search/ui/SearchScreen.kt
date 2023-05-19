package io.foundy.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.found.user.ui.UserProfileDialog
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.component.CamstudyTopAppBar
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.FriendStatus
import io.foundy.core.model.SearchedUser
import io.foundy.core.ui.UserProfileImage
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun SearchRoute(
    navigator: DestinationsNavigator,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var userIdForShowDialog by remember { mutableStateOf<String?>(null) }

    viewModel.collectSideEffect {
        when (it) {
            is SearchSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it.content ?: context.getString(it.defaultStringRes)
                )
            }
        }
    }

    userIdForShowDialog?.let { id ->
        UserProfileDialog(userId = id, onCancel = { userIdForShowDialog = null })
    }

    SearchScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        popBackStack = { navigator.popBackStack() },
        onUserClick = { userIdForShowDialog = it }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onUserClick: (String) -> Unit,
    popBackStack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            CamstudyTopAppBar(
                onBackClick = popBackStack,
                height = 56.dp,
                title = {
                    CamstudyTextField(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .padding(end = 14.dp)
                            .focusRequester(focusRequester),
                        value = uiState.query,
                        onValueChange = uiState.onQueryChanged,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                uiState.onSearchClick(uiState.query)
                                keyboardController?.hide()
                            }
                        ),
                        placeholder = stringResource(R.string.search_text_field_placeholder)
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.searchedUsers.isEmpty()) {
                CamstudyText(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.empty),
                    style = CamstudyTheme.typography.displayMedium.copy(
                        color = CamstudyTheme.colorScheme.systemUi03,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            } else {
                LazyColumn {
                    items(uiState.searchedUsers, key = { it.id }) { user ->
                        UserTile(searchedUser = user, onClick = { onUserClick(user.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun UserTile(
    searchedUser: SearchedUser,
    onClick: () -> Unit
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = CamstudyTheme.colorScheme.systemBackground)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserProfileImage(imageUrl = searchedUser.profileImage)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = searchedUser.name,
                    style = CamstudyTheme.typography.titleSmall.copy(
                        color = CamstudyTheme.colorScheme.systemUi08,
                        fontWeight = FontWeight.Medium
                    )
                )
                searchedUser.introduce?.let {
                    Text(
                        text = it,
                        style = CamstudyTheme.typography.labelMedium.copy(
                            color = CamstudyTheme.colorScheme.systemUi05,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
        CamstudyDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Preview
@Composable
private fun UserTilePreview() {
    CamstudyTheme {
        UserTile(
            searchedUser = SearchedUser(
                id = "id",
                name = "김민성",
                introduce = "안녕하세요",
                profileImage = null,
                friendStatus = FriendStatus.NONE
            ),
            onClick = {}
        )
    }
}
