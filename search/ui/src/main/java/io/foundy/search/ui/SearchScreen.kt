package io.foundy.search.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.FriendStatus
import io.foundy.core.model.SearchedUser
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination
@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    viewModel.collectSideEffect {
        when (it) {
            is SearchSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it.content ?: context.getString(it.defaultStringRes)
                )
            }
        }
    }

    SearchScreen(uiState = uiState, snackbarHostState = snackbarHostState)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    snackbarHostState: SnackbarHostState
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            CamstudyTextField(
                value = uiState.query,
                onValueChange = uiState.onQueryChanged,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        uiState.onSearchClick(uiState.query)
                        keyboardController?.hide()
                    }
                )
            )
            if (uiState.searchedUsers.isEmpty()) {
                Text("텅")
            } else {
                LazyColumn {
                    items(uiState.searchedUsers, key = { it.id }) { user ->
                        User(
                            searchedUser = user,
                            enabledActionButton = uiState.actionPendingUserIds.none {
                                it == user.id
                            },
                            onRequestClick = uiState.onFriendRequestClick,
                            // TODO: Show recheck dialog when click cancel button
                            onCancelRequestClick = uiState.onCancelFriendRequestClick,
                            // TODO: Show recheck dialog when click remove button
                            onRemoveClick = uiState.onRemoveFriendClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun User(
    searchedUser: SearchedUser,
    enabledActionButton: Boolean,
    onRequestClick: (id: String) -> Unit,
    onCancelRequestClick: (id: String) -> Unit,
    onRemoveClick: (id: String) -> Unit
) {
    Row {
        Text(text = searchedUser.name, modifier = Modifier.padding(4.dp))
        when (searchedUser.friendStatus) {
            FriendStatus.NONE -> IconButton(
                onClick = { onRequestClick(searchedUser.id) },
                enabled = enabledActionButton
            ) {
                CamstudyIcon(icon = CamstudyIcons.PersonAdd, contentDescription = null)
            }
            FriendStatus.REQUESTED -> IconButton(
                onClick = { onCancelRequestClick(searchedUser.id) },
                enabled = enabledActionButton
            ) {
                CamstudyIcon(
                    icon = CamstudyIcons.PersonRemove,
                    contentDescription = null
                )
            }
            FriendStatus.ACCEPTED -> IconButton(
                onClick = { onRemoveClick(searchedUser.id) },
                enabled = enabledActionButton
            ) {
                CamstudyIcon(
                    icon = CamstudyIcons.PersonRemove,
                    contentDescription = null,
                    tint = CamstudyTheme.colorScheme.error
                )
            }
        }
    }
}
