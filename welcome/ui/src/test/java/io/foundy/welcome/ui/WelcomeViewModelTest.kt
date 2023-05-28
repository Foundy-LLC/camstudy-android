package io.foundy.welcome.ui

import android.content.Context
import io.foundy.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.core.common.util.ConvertBitmapToFileUseCase
import io.foundy.core.test.MainDispatcherRule
import io.foundy.user.domain.usecase.PostUserInitInfoUseCase
import io.foundy.welcome.ui.fake.FakeAuthRepository
import io.foundy.welcome.ui.fake.FakeUserRepository
import io.foundy.welcome.ui.fake.FakeWelcomeRepository
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.orbitmvi.orbit.liveTest

class WelcomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()

    private val authRepository = FakeAuthRepository()

    private val welcomeRepository = FakeWelcomeRepository()

    private lateinit var viewModel: WelcomeViewModel

    private val context: Context = mock()

    @Test
    fun `should have error when name is empty after update`() {
        initViewModel()
        val subject = viewModel.liveTest { dispatcher = mainDispatcherRule.testDispatcher }
        subject.runOnCreate()

        subject.testIntent {
            updateNameInput("")
        }

        subject.assert(WelcomeUiState()) {
            states(
                { copy(nameErrorMessageRes = R.string.must_input_name_error_message) }
            )
        }
    }

    private fun initViewModel() {
        viewModel = WelcomeViewModel(
            postUserInitInfoUseCase = PostUserInitInfoUseCase(userRepository = userRepository),
            getCurrentUserIdUseCase = GetCurrentUserIdUseCase(authRepository = authRepository),
            welcomeRepository = welcomeRepository,
            convertBitmapToFileUseCase = ConvertBitmapToFileUseCase(context)
        )
    }
}
