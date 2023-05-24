package io.foundy.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import io.foundy.core.designsystem.theme.CamstudyTheme

@Composable
fun CamstudyBottomSheetDialog(
    onDismissRequest: () -> Unit,
    properties: BottomSheetDialogProperties = BottomSheetDialogProperties(),
    content: @Composable () -> Unit
) {
    BottomSheetDialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Box(
            modifier = Modifier.background(color = CamstudyTheme.colorScheme.systemBackground)
        ) {
            content()
        }
    }
}
