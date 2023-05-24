package io.foundy.room.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.CamstudyIcons
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.ui.VerticalGradient
import io.foundy.room.ui.media.FakeMediaManager
import io.foundy.room.ui.media.LocalMediaManager
import io.foundy.room.ui.peer.PeerUiState

@Composable
fun PeerContent(
    modifier: Modifier = Modifier,
    peerState: PeerUiState,
    showMoreButton: Boolean,
    onMoreButtonClick: (id: String, name: String) -> Unit
) {
    val eglBaseContext = LocalMediaManager.current.eglBaseContext

    Box(
        modifier = modifier.background(color = CamstudyTheme.colorScheme.systemUi09),
    ) {
        if (peerState.videoTrack != null) {
            VideoRenderer(
                modifier = Modifier.fillMaxWidth(),
                eglBaseContext = eglBaseContext,
                videoTrack = peerState.videoTrack
            )
            PeerVerticalGradient(
                alignment = Alignment.TopCenter,
                colors = listOf(
                    Color.Black,
                    Color.Transparent
                ),
            )
            PeerVerticalGradient(
                alignment = Alignment.BottomCenter,
                colors = listOf(
                    Color.Transparent,
                    Color.Black
                ),
            )
        } else {
            PeerContentIcon(
                modifier = Modifier.align(Alignment.Center),
                icon = CamstudyIcons.MaterialVideoCamOff,
            )
        }
        Row(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 12.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CamstudyText(
                modifier = Modifier.weight(1f),
                text = peerState.name,
                style = CamstudyTheme.typography.titleMedium.copy(color = Color.White),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            MediaState(
                enabledHeadset = peerState.enabledHeadset,
                enabledMicrophone = peerState.enabledMicrophone
            )
        }
        if (showMoreButton) {
            // TODO: 클릭이 잘 안됨. 하단부만 클릭이 됨
            MoreButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { onMoreButtonClick(peerState.uid, peerState.name) }
            )
        }
    }
}

@Composable
private fun BoxScope.PeerVerticalGradient(
    alignment: Alignment,
    colors: List<Color>
) {
    VerticalGradient(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .align(alignment),
        colors = colors,
        alpha = 0.4f
    )
}

@Composable
private fun MediaState(
    enabledMicrophone: Boolean,
    enabledHeadset: Boolean
) {
    val tint = Color.Unspecified
    val modifier = Modifier.size(24.dp)

    Row {
        if (!enabledHeadset) {
            CamstudyIcon(
                modifier = modifier,
                icon = CamstudyIcons.HeadsetOff,
                tint = tint,
                contentDescription = null
            )
        }
        if (!enabledMicrophone) {
            Box(Modifier.padding(start = 8.dp))
            CamstudyIcon(
                modifier = modifier,
                icon = CamstudyIcons.MicOff,
                tint = tint,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun MoreButton(
    modifier: Modifier,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier.padding(4.dp),
        onClick = onClick,
    ) {
        CamstudyIcon(
            icon = CamstudyIcons.MoreHoriz,
            tint = Color.White,
            contentDescription = null
        )
    }
}

@Preview(showBackground = true, widthDp = 200, heightDp = 300)
@Composable
private fun PeerContentPreview() {
    CompositionLocalProvider(LocalMediaManager provides FakeMediaManager()) {
        CamstudyTheme {
            PeerContent(
                peerState = PeerUiState(
                    uid = "id",
                    name = "홍길동",
                    enabledMicrophone = false,
                    enabledHeadset = false,
                    audioTrack = null,
                    videoTrack = null,
                    isMe = false
                ),
                showMoreButton = true,
                onMoreButtonClick = { _, _ -> }
            )
        }
    }
}
