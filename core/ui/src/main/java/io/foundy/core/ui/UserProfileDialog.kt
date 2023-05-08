package io.foundy.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.component.DialogMaxWidth
import io.foundy.core.designsystem.component.DialogMinWidth
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.User

@Composable
fun UserProfileDialog(
    user: User?,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = Modifier
                .sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth)
                .clip(RoundedCornerShape(16.dp))
                .background(color = CamstudyTheme.colorScheme.cardUi),
        ) {
            UserProfileImage(
                imageUrl = user?.profileImage,
                imageOrContainerSize = 100.dp,
                fallbackIconSize = 64.dp,
                cornerShape = RoundedCornerShape(16.dp)
            )
            CamstudyText(text = user.toString())
        }
    }
}

@Preview
@Composable
private fun UserProfileDialogPreview() {
    CamstudyTheme {
//        UserProfileDialog(
//            user = User(
//                id = "id",
//                name = "김민성",
//                introduce = "자기소개",
//                profileImage = null,
//                rankingScore = 31232,
//                studyTimeSec = 3253,
//                organizations = listOf("구글"),
//                tags = listOf("안드로이드")
//            ),
//            onCancel = {}
//        )
    }
}
