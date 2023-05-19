package io.foundy.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import io.foundy.core.designsystem.R
import io.foundy.core.designsystem.theme.CamstudyTheme

val DialogMinWidth = 280.dp
val DialogMaxWidth = 560.dp

@Composable
fun CamstudyDialog(
    title: String? = null,
    content: String,
    confirmText: String = stringResource(R.string.confirm),
    onDismissRequest: () -> Unit,
    onCancel: (() -> Unit)? = null,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth)
                .clip(RoundedCornerShape(8.dp))
                .background(color = CamstudyTheme.colorScheme.systemBackground),
        ) {
            Column(Modifier.fillMaxWidth()) {
                Texts(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 16.dp),
                    title = title,
                    content = content
                )
                Buttons(
                    modifier = Modifier.padding(8.dp),
                    confirmText = confirmText,
                    onCancelClick = onCancel,
                    onConfirmClick = onConfirm
                )
            }
        }
    }
}

@Composable
private fun Texts(
    modifier: Modifier = Modifier,
    title: String?,
    content: String
) {
    Column(
        modifier = modifier
    ) {
        if (title != null) {
            CamstudyText(
                text = title,
                style = CamstudyTheme.typography.titleLarge.copy(
                    color = CamstudyTheme.colorScheme.systemUi09,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        CamstudyText(
            text = content,
            style = CamstudyTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Normal,
                color = CamstudyTheme.colorScheme.systemUi05
            )
        )
    }
}

@Composable
private fun ColumnScope.Buttons(
    modifier: Modifier = Modifier,
    confirmText: String,
    onCancelClick: (() -> Unit)? = null,
    onConfirmClick: () -> Unit
) {
    Row(modifier.align(Alignment.End)) {
        if (onCancelClick != null) {
            CamstudyTextButton(label = stringResource(R.string.cancel), onClick = onCancelClick)
        }
        CamstudyTextButton(label = confirmText, onClick = onConfirmClick)
    }
}

@Preview(widthDp = 320)
@Composable
private fun CamstudyDialogPreview() {
    CamstudyTheme {
        CamstudyDialog(
            title = "Title 타이틀",
            content = "Dialog 메시지",
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}

@Preview(widthDp = 320)
@Composable
private fun CamstudyDialogOnlyContentPreview() {
    CamstudyTheme {
        CamstudyDialog(
            content = "Dialog 메시지",
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 500, widthDp = 320)
@Composable
private fun CamstudyDialogWorkingPreview() {
    CamstudyTheme {
        var showDialog by remember { mutableStateOf(false) }
        if (showDialog) {
            CamstudyDialog(
                content = "다이어로그",
                onConfirm = {},
                onDismissRequest = { showDialog = false }
            )
        }
        CamstudyTextButton(onClick = { showDialog = true }, label = "show dialog")
    }
}
