package io.foundy.friend.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.friend.ui.FriendRecommendTabUiState
import io.foundy.friend.ui.R

@Composable
fun FriendRecommendContent(
    uiState: FriendRecommendTabUiState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CamstudyText(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            text = stringResource(R.string.in_developing),
            style = CamstudyTheme.typography.headlineSmall.copy(
                color = CamstudyTheme.colorScheme.systemUi04,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
