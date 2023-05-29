package io.foundy.friend.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.UserOverview
import io.foundy.core.ui.UserProfileImage

@Composable
internal fun UserTile(
    user: UserOverview,
    leading: (@Composable () -> Unit)? = null,
    onClick: (UserOverview) -> Unit,
    showDivider: Boolean = true
) {
    Box {
        Row(
            modifier = Modifier
                .background(color = CamstudyTheme.colorScheme.systemBackground)
                .clickable { onClick(user) }
                .padding(top = 12.dp, bottom = 11.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage(imageUrl = user.profileImage)
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CamstudyText(
                    text = user.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = CamstudyTheme.typography.titleSmall.copy(
                        color = CamstudyTheme.colorScheme.systemUi08,
                        fontWeight = FontWeight.Medium
                    )
                )
                user.introduce?.let { introduce ->
                    CamstudyText(
                        text = introduce,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = CamstudyTheme.typography.labelMedium.copy(
                            color = CamstudyTheme.colorScheme.systemUi05,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            leading?.invoke()
        }
        if (showDivider) {
            CamstudyDivider(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun ProfileImage(imageUrl: String?) {
    UserProfileImage(
        imageOrContainerSize = 40.dp,
        fallbackIconSize = 24.dp,
        model = imageUrl
    )
}

@Preview
@Composable
private fun UserTilePreview() {
    CamstudyTheme {
        UserTile(
            user = UserOverview(
                id = "idid",
                name = "김민성",
                introduce = "안녕하세용",
                profileImage = null
            ),
            onClick = {}
        )
    }
}
