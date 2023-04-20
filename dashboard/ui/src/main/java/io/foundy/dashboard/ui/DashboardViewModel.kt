package io.foundy.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.foundy.auth.data.repository.AuthRepository
import io.foundy.crop.data.repository.CropRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cropRepository: CropRepository
) : ViewModel(), ContainerHost<DashboardUiState, DashboardSideEffect> {

    override val container: Container<DashboardUiState, DashboardSideEffect> = container(
        DashboardUiState(fetchGrowingCrop = ::fetchGrowingCrop)
    )

    private lateinit var currentUserId: String

    init {
        viewModelScope.launch {
            currentUserId = requireNotNull(authRepository.currentUserIdStream.firstOrNull()) {
                "현재 회원 아이디를 얻을 수 없습니다. 로그인 하지 않고 대시보드에 접근했습니다."
            }
            fetchGrowingCrop()
        }
        viewModelScope.launch {
            cropRepository.currentUserGrowingCropFlow.collectLatest { growingCrop ->
                intent {
                    reduce {
                        state.copy(
                            growingCropUiState = GrowingCropUiState.Success(
                                growingCrop = growingCrop
                            )
                        )
                    }
                }
            }
        }
    }

    private fun fetchGrowingCrop() = intent {
        cropRepository.getGrowingCrop(userId = currentUserId)
            .onFailure { throwable ->
                reduce {
                    state.copy(
                        growingCropUiState = GrowingCropUiState.Failure(throwable.message)
                    )
                }
            }
    }
}
