package io.foundy.setting.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor() :
    ViewModel(), ContainerHost<EditProfileUiState, EditProfileSideEffect> {

    override val container: Container<EditProfileUiState, EditProfileSideEffect> = container(
        EditProfileUiState()
    )

    fun bind(
        name: String,
        introduce: String?,
        imageUrl: String?,
        tags: List<String>
    ) = intent {
        if (state.didBind) {
            return@intent
        }
        reduce {
            state.copy(
                didBind = true,
                name = name,
                introduce = introduce ?: "",
                imageUrl = imageUrl,
                tags = tags
            )
        }
    }
}
