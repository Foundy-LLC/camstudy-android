package io.found.user.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import io.found.user.ui.UserProfileDialogProperty.ProfileShape
import io.found.user.ui.UserProfileDialogProperty.ProfileSize
import io.foundy.core.designsystem.component.CamstudyContainedButton
import io.foundy.core.designsystem.component.CamstudyDialog
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyOutlinedButton
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.DialogMaxWidth
import io.foundy.core.designsystem.component.DialogMinWidth
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType
import io.foundy.core.model.FriendStatus
import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.HarvestedCrop
import io.foundy.core.model.User
import io.foundy.core.ui.UserProfileImage
import io.foundy.core.ui.UserProfileInfoGroup
import io.foundy.user.ui.R
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.util.Date

private object UserProfileDialogProperty {
    val ProfileShape = RoundedCornerShape(16.dp)
    val ProfileSize = 100.dp
}

@Composable
fun UserProfileDialog(
    viewModel: UserProfileDialogViewModel = hiltViewModel(),
    userId: String,
    onCancel: () -> Unit,
    onDidRequestFriend: ((User) -> Unit)? = null,
) {
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect {
        when (it) {
            is UserProfileDialogSideEffect.DidRequestFriend -> {
                onDidRequestFriend?.invoke(it.user)
            }
        }
    }

    LaunchedEffect(userId) {
        viewModel.fetchUser(id = userId)
    }

    UserProfileDialogContent(uiState = uiState, onCancel = onCancel)
}

@Composable
private fun UserProfileDialogContent(
    uiState: UserProfileDialogUiState,
    onCancel: () -> Unit
) {
    // TODO: 다이어로그가 recomposition 되었을때 크기가 변하지 않는 버그가 존재함
    //  다음과 같은 방법으로 임시 해결 해놓음 https://stackoverflow.com/a/71287474/14434806
    //  추후에 버그가 수정되면 Box로 감싼 부분을 제거할 것.
    Dialog(onDismissRequest = onCancel) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCancel
                )
        ) {
            Column(
                modifier = Modifier
                    .sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = CamstudyTheme.colorScheme.cardUi)
                    .clickable(
                        onClick = {},
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
                    .padding(top = 28.dp, bottom = 20.dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (uiState) {
                    is UserProfileDialogUiState.Failure -> CamstudyText(
                        text = uiState.message.content
                            ?: stringResource(id = uiState.message.defaultRes)
                    )
                    UserProfileDialogUiState.Loading -> UserProfileDialogShimmer()
                    is UserProfileDialogUiState.Success -> SuccessContent(uiState)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.SuccessContent(uiState: UserProfileDialogUiState.Success) {
    val user = uiState.user

    var showFriendCancelRecheckDialog by remember { mutableStateOf(false) }

    if (showFriendCancelRecheckDialog) {
        CamstudyDialog(
            content = stringResource(R.string.sure_you_want_to_cancel_friend),
            onConfirm = {
                uiState.onCancelFriend()
                showFriendCancelRecheckDialog = false
            },
            onCancel = { showFriendCancelRecheckDialog = false },
            onDismissRequest = { showFriendCancelRecheckDialog = false },
            confirmText = stringResource(id = R.string.cancel_friend)
        )
    }

    UserProfileImage(
        model = user.profileImage,
        imageOrContainerSize = ProfileSize,
        fallbackIconSize = 64.dp,
        cornerShape = ProfileShape
    )
    Spacer(modifier = Modifier.height(20.dp))
    NameAndOrganization(
        name = user.name,
        organization = user.organizations.firstOrNull()
    )
    if (user.introduce != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Introduce(introduce = user.introduce!!)
    }
    Spacer(modifier = Modifier.height(8.dp))
    Tags(tags = user.tags)
    Spacer(modifier = Modifier.height(20.dp))
    CamstudyDivider()
    Spacer(modifier = Modifier.height(14.dp))
    UserProfileInfoGroup(
        weeklyRankingOverall = user.weeklyRankingOverall,
        weeklyStudyTimeSec = user.weeklyStudyTimeSec,
        weeklyRanking = user.weeklyRanking,
        consecutiveStudyDays = user.consecutiveStudyDays,
        growingCrop = user.growingCrop,
        harvestedCrops = user.harvestedCrops
    )
    if (!user.isMe) {
        Spacer(modifier = Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .align(Alignment.End)
        ) {
            when (user.friendStatus) {
                FriendStatus.NONE -> CamstudyContainedButton(
                    onClick = uiState.onRequestFriend,
                    enabled = uiState.enabledFriendActionButton,
                    enableLabelSizeAnimation = true,
                    leadingIcon = uiState.friendActionLeadingIcon ?: CamstudyIcons.PersonAdd,
                    label = stringResource(
                        uiState.friendActionMessageRes ?: R.string.request_friend
                    )
                )
                FriendStatus.REQUESTED -> CamstudyOutlinedButton(
                    onClick = uiState.onCancelFriendRequest,
                    enabled = uiState.enabledFriendActionButton,
                    enableLabelSizeAnimation = true,
                    leadingIcon = uiState.friendActionLeadingIcon
                        ?: CamstudyIcons.CancelScheduleSend,
                    label = stringResource(
                        uiState.friendActionMessageRes ?: R.string.cancel_friend_request
                    )
                )
                FriendStatus.ACCEPTED -> CamstudyOutlinedButton(
                    onClick = {
                        showFriendCancelRecheckDialog = true
                    },
                    enableLabelSizeAnimation = true,
                    leadingIcon = uiState.friendActionLeadingIcon ?: CamstudyIcons.PersonRemove,
                    enabled = uiState.enabledFriendActionButton,
                    label = stringResource(
                        uiState.friendActionMessageRes ?: R.string.cancel_friend
                    )
                )
            }
        }
    }
}

@Composable
private fun NameAndOrganization(name: String, organization: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CamstudyText(
            text = name,
            style = CamstudyTheme.typography.titleLarge.copy(
                color = CamstudyTheme.colorScheme.systemUi08,
                fontWeight = FontWeight.SemiBold
            )
        )
        if (organization != null) {
            Spacer(modifier = Modifier.width(8.dp))
            CamstudyText(
                text = organization,
                style = CamstudyTheme.typography.titleSmall.copy(
                    color = CamstudyTheme.colorScheme.primaryPress,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun Introduce(introduce: String) {
    CamstudyText(
        text = introduce,
        style = CamstudyTheme.typography.titleMedium.copy(
            color = CamstudyTheme.colorScheme.systemUi04,
            fontWeight = FontWeight.Normal
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Tags(tags: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.Center
    ) {
        for (tag in tags) {
            TagItem(tag = tag)
        }
    }
}

@Composable
private fun TagItem(tag: String) {
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = CamstudyTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        CamstudyText(
            text = tag,
            style = CamstudyTheme.typography.titleSmall.copy(
                color = CamstudyTheme.colorScheme.primaryPress,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
private fun UserProfileDialogShimmer() {
    val shimmerColor = CamstudyTheme.colorScheme.systemUi01
    val smallShimmerClip = RoundedCornerShape(6.dp)
    val mediumShimmerClip = RoundedCornerShape(10.dp)
    val infoTileCount = 4

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(ProfileSize)
                .clip(ProfileShape)
                .background(color = shimmerColor)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            Modifier
                .size(height = 28.dp, width = 160.dp)
                .clip(mediumShimmerClip)
                .background(color = shimmerColor)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            Modifier
                .size(height = 22.dp, width = 240.dp)
                .clip(smallShimmerClip)
                .background(color = shimmerColor)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            Modifier
                .size(height = 34.dp, width = 132.dp)
                .clip(mediumShimmerClip)
                .background(color = shimmerColor)
        )
        Spacer(modifier = Modifier.height(24.dp))
        CamstudyDivider()
        Spacer(modifier = Modifier.height(24.dp))
        repeat(infoTileCount) { index ->
            Box(
                Modifier
                    .height(24.dp)
                    .fillMaxWidth()
                    .clip(smallShimmerClip)
                    .background(color = shimmerColor)
            )
            if (infoTileCount - 1 != index) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Box(
            Modifier
                .align(Alignment.End)
                .size(height = 38.dp, width = 120.dp)
                .clip(mediumShimmerClip)
                .background(color = shimmerColor)
        )
    }
}

@Preview
@Composable
private fun UserProfileDialogPreview() {
    CamstudyTheme {
        UserProfileDialogContent(
            uiState = UserProfileDialogUiState.Success(
                user = User(
                    id = "id",
                    name = "김민성",
                    isMe = false,
                    introduce = "안녕하세요",
                    profileImage = null,
                    weeklyRanking = 23,
                    totalRanking = 40,
                    weeklyStudyTimeSec = 23142,
                    weeklyRankingOverall = 42,
                    growingCrop = GrowingCrop(
                        id = "gid",
                        ownerId = "id",
                        type = CropType.PUMPKIN,
                        level = 2,
                        expectedGrade = CropGrade.GOLD,
                        isDead = false,
                        plantedAt = Date()
                    ),
                    harvestedCrops = listOf(
                        HarvestedCrop(
                            type = CropType.TOMATO,
                            grade = CropGrade.SILVER,
                            plantedAt = Date(),
                            harvestedAt = Date()
                        ),
                        HarvestedCrop(
                            type = CropType.STRAWBERRY,
                            grade = CropGrade.FRESH,
                            plantedAt = Date(),
                            harvestedAt = Date()
                        ),
                        HarvestedCrop(
                            type = CropType.STRAWBERRY,
                            grade = CropGrade.FRESH,
                            plantedAt = Date(),
                            harvestedAt = Date()
                        ),
                        HarvestedCrop(
                            type = CropType.STRAWBERRY,
                            grade = CropGrade.FRESH,
                            plantedAt = Date(),
                            harvestedAt = Date()
                        ),
                        HarvestedCrop(
                            type = CropType.STRAWBERRY,
                            grade = CropGrade.FRESH,
                            plantedAt = Date(),
                            harvestedAt = Date()
                        ),
                        HarvestedCrop(
                            type = CropType.STRAWBERRY,
                            grade = CropGrade.FRESH,
                            plantedAt = Date(),
                            harvestedAt = Date()
                        )
                    ),
                    organizations = listOf("한성대학교"),
                    tags = listOf("안드로이드", "개발", "웹"),
                    consecutiveStudyDays = 4,
                    friendStatus = FriendStatus.NONE
                ),
                onRequestFriend = {},
                onCancelFriendRequest = {},
                onCancelFriend = {}
            ),
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    CamstudyTheme {
        UserProfileDialogContent(
            uiState = UserProfileDialogUiState.Loading,
            onCancel = {}
        )
    }
}
