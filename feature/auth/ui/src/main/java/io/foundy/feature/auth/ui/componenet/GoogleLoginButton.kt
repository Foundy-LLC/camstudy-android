package io.foundy.feature.auth.ui.componenet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.feature.auth.ui.R

private val ButtonShape = RoundedCornerShape(6.dp)

@Composable
fun GoogleLoginButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    showProgressIndicator: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(width = 1.dp, color = CamstudyTheme.colorScheme.systemUi03),
                shape = ButtonShape
            )
            .shadow(elevation = 2.dp, shape = ButtonShape)
            .background(
                color = if (enabled) {
                    CamstudyTheme.colorScheme.systemBackground
                } else {
                    CamstudyTheme.colorScheme.systemUi01
                }
            )
            .clickable(onClick = onClick, enabled = enabled)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = showProgressIndicator) {
                Row {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
            Image(
                painter = painterResource(id = R.drawable.mark_google),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            CamstudyText(
                text = "Google로 로그인",
                style = CamstudyTheme.typography.titleMedium.copy(
                    color = CamstudyTheme.colorScheme.systemUi09
                )
            )
        }
    }
}

@Preview
@Composable
fun GoogleLoginButtonPreview() {
    CamstudyTheme {
        GoogleLoginButton(
            showProgressIndicator = true,
            onClick = {}
        )
    }
}
