package io.foundy.ranking.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.foundy.core.common.util.formatDuration
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.UserRankingOverview
import io.foundy.ranking.ui.R

private fun getRankingNumberBoxColorBy(ranking: Int): Color {
    return when (ranking) {
        1 -> Color(0xFFE2B63E)
        2 -> Color(0xFFA6C0D3)
        3 -> Color(0xFFBA7638)
        else -> Color(0xFF988363)
    }
}

@Composable
fun RankingTile(user: UserRankingOverview) {
    var expanded by remember { mutableStateOf(false) }

    RankingTileContent(
        user = user,
        expanded = expanded,
        onExpandClick = { expanded = !expanded }
    )
}

@Composable
private fun RankingTileContent(
    user: UserRankingOverview,
    expanded: Boolean,
    onExpandClick: () -> Unit
) {
    Column(
        modifier = Modifier.background(color = CamstudyTheme.colorScheme.systemBackground)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 7.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RankingNumber(ranking = user.ranking)
            Spacer(modifier = Modifier.width(12.dp))
            ProfileImage(imageUrl = user.profileImage)
            Spacer(modifier = Modifier.width(12.dp))
            UserNameAndIntroduce(
                modifier = Modifier.weight(1f),
                name = user.name,
                introduce = user.introduce
            )
            Spacer(modifier = Modifier.width(12.dp))
            ExpandButton(expanded = expanded, onClick = onExpandClick)
        }
        AnimatedVisibility(
            visible = expanded
        ) {
            RankingDetail(
                score = user.score,
                studyTimeSec = user.studyTimeSec
            )
        }
    }
}

@Composable
private fun RankingNumber(ranking: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .size(40.dp)
            .background(getRankingNumberBoxColorBy(ranking = ranking))
    ) {
        CamstudyText(
            text = ranking.toString(),
            modifier = Modifier.align(Alignment.Center),
            style = CamstudyTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun ProfileImage(imageUrl: String?) {
    val thumbnailModifier = Modifier
        .size(40.dp)
        .clip(RoundedCornerShape(8.dp))

    if (imageUrl != null) {
        AsyncImage(
            modifier = thumbnailModifier,
            model = imageUrl,
            contentDescription = null
        )
    } else {
        Box(
            modifier = thumbnailModifier.background(color = CamstudyTheme.colorScheme.systemUi01)
        ) {
            CamstudyIcon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center),
                icon = CamstudyIcons.Person,
                tint = CamstudyTheme.colorScheme.systemUi03,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun UserNameAndIntroduce(modifier: Modifier, name: String, introduce: String) {
    Column(modifier = modifier) {
        CamstudyText(
            text = name,
            style = CamstudyTheme.typography.titleSmall.copy(
                color = CamstudyTheme.colorScheme.systemUi08,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        CamstudyText(
            text = introduce,
            style = CamstudyTheme.typography.labelMedium.copy(
                color = CamstudyTheme.colorScheme.systemUi05,
                fontWeight = FontWeight.Normal
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ExpandButton(expanded: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        CamstudyIcon(
            modifier = Modifier.size(24.dp),
            icon = if (expanded) CamstudyIcons.KeyboardArrowUp else CamstudyIcons.KeyboardArrowDown,
            contentDescription = null,
            tint = CamstudyTheme.colorScheme.systemUi08
        )
    }
}

@Composable
private fun RankingDetail(score: Int, studyTimeSec: Int) {
    val infoTextStyle = CamstudyTheme.typography.titleSmall.copy(
        color = CamstudyTheme.colorScheme.systemUi07,
        fontWeight = FontWeight.Normal
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemUi01)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CamstudyText(
            text = stringResource(R.string.ranking_detail),
            style = CamstudyTheme.typography.titleSmall.copy(
                color = CamstudyTheme.colorScheme.systemUi08,
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(modifier = Modifier.width(20.dp))
        CamstudyText(
            text = stringResource(R.string.score_format, "%,d".format(score)),
            style = infoTextStyle
        )
        Spacer(modifier = Modifier.width(12.dp))
        CamstudyText(
            text = studyTimeSec.formatDuration(),
            style = infoTextStyle
        )
    }
}

@Preview
@Composable
private fun RankingTilePreview() {
    CamstudyTheme {
        RankingTileContent(
            user = UserRankingOverview(
                id = "id",
                name = "홍길동",
                ranking = 1,
                profileImage = null,
                introduce = "안녕하세요",
                score = 12412,
                studyTimeSec = 12032
            ),
            expanded = false,
            onExpandClick = {}
        )
    }
}

@Preview
@Composable
private fun ExpandedRankingTilePreview() {
    var expanded by remember { mutableStateOf(true) }
    CamstudyTheme {
        RankingTileContent(
            user = UserRankingOverview(
                id = "id",
                name = "홍길동",
                ranking = 1,
                profileImage = null,
                introduce = "안녕하세요",
                score = 12412,
                studyTimeSec = 12032
            ),
            expanded = expanded,
            onExpandClick = { expanded = !expanded }
        )
    }
}