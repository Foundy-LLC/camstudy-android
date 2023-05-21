package io.foundy.setting.ui.profile

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.designsystem.component.CamstudyText
import kotlinx.parcelize.Parcelize
import org.orbitmvi.orbit.compose.collectAsState

@Parcelize
class StringList(
    private val list: List<String>
) : Parcelable, List<String> by list

@Composable
@Destination
fun EditProfileRoute(
    name: String,
    introduce: String?,
    imageUrl: String?,
    tags: StringList,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.bind(
            name = name,
            introduce = introduce,
            imageUrl = imageUrl,
            tags = tags
        )
    }

    EditProfileScreen(uiState = uiState)
}

@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState
) {
    CamstudyText(text = uiState.toString())
}
