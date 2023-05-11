package io.foundy.room_list.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import io.foundy.core.common.util.toBitmap
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyExtendedFloatingActionButton
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.component.FloatingActionButtonBottomPadding
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.constant.RoomConstants
import io.foundy.core.ui.RoomTileWithJoinButton
import io.foundy.core.ui.isScrollingUp
import io.foundy.room.ui.RoomActivity
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.util.Date

@Composable
fun RoomListRoute(
    viewModel: RoomListViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val rooms = uiState.roomPagingDataStream.collectAsLazyPagingItems()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectSideEffect {
        when (it) {
            is RoomListSideEffect.Message -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(it.content ?: context.getString(it.defaultRes))
            }
            is RoomListSideEffect.SuccessToCreateRoom -> {
                val intent = RoomActivity.getIntent(context, roomOverview = it.createdRoom)
                context.startActivity(intent)
            }
        }
    }

    RoomListScreen(
        uiState = uiState,
        rooms = rooms,
        snackbarHostState = snackbarHostState,
        onRoomJoinClick = { room ->
            val intent = RoomActivity.getIntent(context, roomOverview = room)
            context.startActivity(intent)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    uiState: RoomListUiState,
    rooms: LazyPagingItems<RoomOverview>,
    snackbarHostState: SnackbarHostState,
    onRoomJoinClick: (room: RoomOverview) -> Unit,
) {
    val roomCreateInput = uiState.roomCreateInput
    val listState = rememberLazyListState()
    var showRoomCreateBottomSheet by remember { mutableStateOf(false) }

    if (showRoomCreateBottomSheet) {
        RoomCreateBottomSheet(
            inputUiState = roomCreateInput,
            onDismissRequest = { showRoomCreateBottomSheet = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = CamstudyTheme.colorScheme.systemBackground,
        floatingActionButton = {
            CamstudyExtendedFloatingActionButton(
                onClick = { showRoomCreateBottomSheet = true },
                expanded = listState.isScrollingUp(),
                text = { Text(text = stringResource(R.string.create_study_room)) },
                icon = {
                    CamstudyIcon(icon = CamstudyIcons.Add, contentDescription = null)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            state = listState
        ) {
            headerItem(query = uiState.searchQuery, onQueryChange = uiState.onSearchQueryChange)
            items(
                items = rooms,
                key = { it.id }
            ) { roomOverview ->
                if (roomOverview == null) {
                    return@items
                }
                Box {
                    RoomTileWithJoinButton(
                        modifier = Modifier.fillMaxWidth(),
                        room = roomOverview,
                        onJoinClick = onRoomJoinClick
                    )
                    CamstudyDivider()
                }
            }
            item { CamstudyDivider() }
            when (rooms.loadState.refresh) { // FIRST LOAD
                is LoadState.Error -> errorItem(rooms.loadState)
                is LoadState.Loading -> loadingItem()
                else -> {}
            }
            when (rooms.loadState.append) { // Pagination
                is LoadState.Error -> errorItem(rooms.loadState)
                is LoadState.Loading -> loadingItem()
                else -> {}
            }
            item { Spacer(modifier = Modifier.height(FloatingActionButtonBottomPadding)) }
        }
    }
}

private fun LazyListScope.headerItem(
    query: String,
    onQueryChange: (String) -> Unit
) {
    item {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 12.dp
            )
        ) {
            CamstudyText(
                text = stringResource(R.string.study_room_list_title),
                style = CamstudyTheme.typography.titleMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi07
                )
            )
            Box(Modifier.height(12.dp))
            CamstudyTextField(
                value = query,
                // TODO: IME action 변경하기
                onValueChange = onQueryChange,
                placeholder = stringResource(R.string.room_search_placeholder)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomCreateBottomSheet(
    inputUiState: RoomCreateInputUiState,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        val bitmap = uri.toBitmap(context)
        inputUiState.onThumbnailChange(bitmap)
    }
    val titleErrorMessage = remember(inputUiState.isTitleLengthValid) {
        if (!inputUiState.isTitleLengthValid) {
            context.getString(R.string.title_length_error_message, RoomConstants.MaxTitleLength)
        } else null
    }
    val passwordErrorMessage = remember(inputUiState.isPasswordLengthValid) {
        if (!inputUiState.isPasswordLengthValid) {
            context.getString(R.string.password_error_message, RoomConstants.MaxPasswordLength)
        } else null
    }
    val timerLengthErrorMessage = remember(inputUiState.isTimerValid) {
        if (!inputUiState.isTimerValid) {
            context.getString(
                R.string.timer_error_message,
                RoomConstants.TimerLengthRange.first,
                RoomConstants.TimerLengthRange.last
            )
        } else null
    }
    val shortBreakLengthErrorMessage = remember(inputUiState.isShortBreakValid) {
        if (!inputUiState.isShortBreakValid) {
            context.getString(
                R.string.short_break_error_message,
                RoomConstants.ShortBreakLengthRage.first,
                RoomConstants.ShortBreakLengthRage.last
            )
        } else null
    }
    val longBreakLengthErrorMessage = remember(inputUiState.isLongBreakValid) {
        if (!inputUiState.isLongBreakValid) {
            context.getString(
                R.string.long_break_error_message,
                RoomConstants.LongBreakLengthRange.first,
                RoomConstants.LongBreakLengthRange.last
            )
        } else null
    }
    val longBreakIntervalLengthErrorMessage = remember(inputUiState.isLongBreakIntervalValid) {
        if (!inputUiState.isLongBreakIntervalValid) {
            context.getString(
                R.string.long_break_interval_error_message,
                RoomConstants.LongBreakIntervalRange.first,
                RoomConstants.LongBreakIntervalRange.last
            )
        } else null
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(
            initialDisplayedMonthMillis = inputUiState.expiredAt.time
        )
        val currentTimeMillis = remember { System.currentTimeMillis() }
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { inputUiState.onExpiredDateChange(Date(it)) }
                        showDatePicker = false
                    }
                ) {
                    Text(text = "선택")
                }
            }
        ) {
            DatePicker(
                state = state,
                title = { Text(text = "공부방 종료일 선택") },
                dateValidator = {
                    it >= currentTimeMillis &&
                        it <= (currentTimeMillis + (1000 * 60 * 60 * 24 * 30L))
                }
            )
        }
    }

    BottomSheetDialog(onDismissRequest = onDismissRequest) {
        Column {
            CamstudyTextField(
                value = inputUiState.title,
                onValueChange = inputUiState.onTitleChange,
                isError = titleErrorMessage != null,
                supportingText = { titleErrorMessage?.let { Text(text = it) } },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                placeholder = {
                    Text(text = "이름")
                }
            )
            CamstudyTextField(
                value = inputUiState.password,
                onValueChange = inputUiState.onPasswordChange,
                isError = passwordErrorMessage != null,
                supportingText = { passwordErrorMessage?.let { Text(text = it) } },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                placeholder = {
                    Text(text = "비밀번호")
                }
            )
            AsyncImage(
                // TODO: Handle fallback
                modifier = Modifier
                    .size(100.dp)
                    .clickable { launcher.launch("image/*") },
                model = inputUiState.thumbnail,
                contentDescription = null
            )
            CamstudyTextField(
                value = inputUiState.timer,
                onValueChange = inputUiState.onTimerChange,
                isError = timerLengthErrorMessage != null,
                supportingText = { timerLengthErrorMessage?.let { Text(text = it) } },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                placeholder = {
                    Text(text = "집중 타이머 길이")
                }
            )
            CamstudyTextField(
                value = inputUiState.shortBreak,
                onValueChange = inputUiState.onShortBreakChange,
                isError = shortBreakLengthErrorMessage != null,
                supportingText = { shortBreakLengthErrorMessage?.let { Text(text = it) } },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                placeholder = {
                    Text(text = "짧은 휴식 길이")
                }
            )
            CamstudyTextField(
                value = inputUiState.longBreak,
                onValueChange = inputUiState.onLongBreakChange,
                isError = longBreakLengthErrorMessage != null,
                supportingText = { longBreakLengthErrorMessage?.let { Text(text = it) } },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                placeholder = {
                    Text(text = "긴 휴식 길이")
                }
            )
            CamstudyTextField(
                value = inputUiState.longBreakInterval,
                onValueChange = inputUiState.onLongBreakIntervalChange,
                isError = longBreakIntervalLengthErrorMessage != null,
                supportingText = { longBreakIntervalLengthErrorMessage?.let { Text(text = it) } },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        inputUiState.onCreateClick()
                        onDismissRequest()
                    }
                ),
                placeholder = {
                    Text(text = "긴 휴식 주기")
                }
            )
            Text(
                modifier = Modifier.clickable {
                    showDatePicker = true
                },
                text = "종료일 ${inputUiState.expiredAt}"
            )
            Button(
                onClick = {
                    inputUiState.onCreateClick()
                    onDismissRequest()
                },
                enabled = inputUiState.canSave
            ) {
                Text(text = "만들기")
            }
        }
    }
}

private fun LazyListScope.errorItem(loadState: CombinedLoadStates) {
    val error = loadState.refresh as LoadState.Error
    item {
        val message = error.error.message ?: stringResource(R.string.unknown_error_caused)
        Text(text = message)
    }
}

private fun LazyListScope.loadingItem() {
    item {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Pagination Loading")
            CircularProgressIndicator()
        }
    }
}
