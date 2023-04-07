package io.foundy.room_list.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.core.common.util.toBitmap
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.CamstudyTextField
import io.foundy.core.designsystem.component.ContainedButton
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.designsystem.util.nonScaledSp
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.UserOverview
import io.foundy.core.model.constant.RoomConstants
import io.foundy.organization.ui.destinations.OrganizationRouteDestination
import io.foundy.room.ui.RoomActivity
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.util.Date

@Destination
@Composable
fun RoomListRoute(
    navigator: DestinationsNavigator,
    viewModel: RoomListViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value
    val rooms = uiState.roomPagingDataStream.collectAsLazyPagingItems()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    viewModel.collectSideEffect {
        when (it) {
            is RoomListSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(it.content ?: context.getString(it.defaultRes))
            }
            RoomListSideEffect.SuccessToCreateRoom -> coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(context.getString(R.string.success_to_create_room))
                // TODO: Refresh is NOT working!!!
                rooms.refresh()
            }
        }
    }

    RoomListScreen(
        rooms = rooms,
        roomCreateInput = uiState.roomCreateInput,
        snackbarHostState = snackbarHostState,
        onRoomClick = { id ->
            val intent = RoomActivity.getIntent(context, roomId = id)
            context.startActivity(intent)
        },
        onOrganizationClick = {
            // TODO: 아래 코드 삭제시 organization module 의존성 제거하기
            navigator.navigate(OrganizationRouteDestination)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    rooms: LazyPagingItems<RoomOverview>,
    roomCreateInput: RoomCreateInputUiState,
    snackbarHostState: SnackbarHostState,
    onRoomClick: (id: String) -> Unit,
    onOrganizationClick: () -> Unit
) {
    var showRoomCreateBottomSheet by remember { mutableStateOf(false) }

    if (showRoomCreateBottomSheet) {
        RoomCreateBottomSheet(
            inputUiState = roomCreateInput,
            onDismissRequest = { showRoomCreateBottomSheet = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item {
                TextButton(onClick = { showRoomCreateBottomSheet = true }) {
                    Text(text = "방 만들기")
                }
            }
            // TODO: 개발용 버튼임 제거해야함
            item {
                TextButton(onClick = onOrganizationClick) {
                    Text(text = "회사, 학교 조회")
                }
            }
            item {
                Text("방목록")
            }
            items(
                items = rooms,
                key = { it.id }
            ) { roomOverview ->
                if (roomOverview == null) {
                    Text("End")
                    return@items
                }
                RoomItem(room = roomOverview, onJoinClick = { onRoomClick(roomOverview.id) })
            }
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
        }
    }
}

@Composable
private fun RoomItem(
    room: RoomOverview,
    onJoinClick: () -> Unit
) {
    Surface(color = CamstudyTheme.colorScheme.systemBackground) {
        Column {
            Row(modifier = Modifier.padding(16.dp)) {
                ThumbnailImage(
                    imageUrl = room.thumbnail,
                    contentDescription = stringResource(R.string.room_thumbnail, room.title)
                )
                Box(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1.0f)) {
                    RoomTitle(title = room.title, isPrivate = room.hasPassword)
                    Box(modifier = Modifier.height(2.dp))
                    Tags(tags = room.tags)
                    Box(modifier = Modifier.height(4.dp))
                    JoinerImages(
                        joinerImages = room.joinedUsers.map { it.profileImage },
                        maxCount = room.maxCount
                    )
                }
                Box(modifier = Modifier.width(28.dp))
                ContainedButton(
                    modifier = Modifier.align(Alignment.Bottom),
                    label = stringResource(R.string.join),
                    onClick = onJoinClick
                )
            }
            Divider(color = CamstudyTheme.colorScheme.systemUi03, thickness = 0.5.dp)
        }
    }
}

@Composable
fun ThumbnailImage(imageUrl: String?, contentDescription: String) {
    val thumbnailModifier = Modifier
        .size(64.dp)
        .clip(RoundedCornerShape(12.dp))

    if (imageUrl != null) {
        AsyncImage(
            modifier = thumbnailModifier,
            model = imageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = contentDescription
        )
    } else {
        Surface(
            modifier = thumbnailModifier,
            color = CamstudyTheme.colorScheme.systemUi02
        ) {}
    }
}

@Composable
private fun RoomTitle(title: String, isPrivate: Boolean) {
    val titleMedium = CamstudyTheme.typography.titleMedium
    val color = CamstudyTheme.colorScheme.systemUi08

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CamstudyText(
            modifier = Modifier.height(22.dp),
            text = title,
            style = titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = titleMedium.fontSize.value.nonScaledSp,
                color = color
            ),
        )
        if (isPrivate) {
            Box(modifier = Modifier.width(4.dp))
            CamstudyIcon(
                modifier = Modifier
                    .size(20.dp)
                    .padding(2.dp),
                icon = CamstudyIcons.LockSharp,
                tint = color,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Tags(tags: List<String>) {
    val labelMedium = CamstudyTheme.typography.labelMedium

    CamstudyText(
        modifier = Modifier.height(16.dp),
        text = tags.map { "#$it" }.joinToString(" ") { it },
        style = labelMedium.copy(
            color = CamstudyTheme.colorScheme.systemUi05,
            fontSize = labelMedium.fontSize.value.nonScaledSp
        )
    )
}

@Composable
private fun JoinerImages(joinerImages: List<String?>, maxCount: Int) {
    Row {
        val modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))

        repeat(maxCount) { index ->
            val isLast = index != maxCount - 1
            val rightPaddingBox: @Composable () -> Unit = {
                if (isLast) Box(modifier = Modifier.width(4.dp))
            }

            if (joinerImages.size <= index) {
                Row {
                    Surface(
                        modifier = modifier,
                        color = CamstudyTheme.colorScheme.systemUi01
                    ) {}
                    rightPaddingBox()
                }
                return@repeat
            }

            Row {
                Surface(
                    modifier = modifier,
                    color = CamstudyTheme.colorScheme.systemUi01
                ) {
                    AsyncImage(
                        modifier = modifier,
                        model = joinerImages[index],
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(
                            id = io.foundy.core.designsystem.R.drawable.ic_person
                        ),
                        contentDescription = null
                    )
                }
                rightPaddingBox()
            }
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

@Preview(fontScale = 1.0f)
@Composable
private fun RoomItemPreview() {
    CamstudyTheme {
        RoomItem(
            room = RoomOverview(
                id = "id",
                title = "공시족 모여라",
                masterId = "id",
                hasPassword = false,
                thumbnail = null,
                joinCount = 1,
                joinedUsers = listOf(
                    UserOverview(id = "id", name = "김민성", profileImage = null, introduce = null)
                ),
                maxCount = 4,
                tags = listOf("공시", "자격증")
            ),
            onJoinClick = {}
        )
    }
}

@Preview
@Composable
private fun PrivateRoomItemPreview() {
    CamstudyTheme {
        RoomItem(
            room = RoomOverview(
                id = "id",
                title = "공시족 모여라",
                masterId = "id",
                hasPassword = true,
                thumbnail = null,
                joinCount = 1,
                joinedUsers = listOf(
                    UserOverview(id = "id", name = "김민성", profileImage = null, introduce = null)
                ),
                maxCount = 4,
                tags = listOf("공시", "자격증")
            ),
            onJoinClick = {}
        )
    }
}
