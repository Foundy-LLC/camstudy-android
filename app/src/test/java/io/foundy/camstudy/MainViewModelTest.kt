package io.foundy.camstudy

import dagger.hilt.android.testing.HiltAndroidTest
import io.foundy.camstudy.fake.FakeAuthRepository
import io.foundy.core.test.MainDispatcherRule
import io.foundy.feature.auth.domain.model.AuthState
import io.foundy.feature.auth.domain.usecase.GetAuthStateStreamUseCase
import io.foundy.feature.auth.ui.destinations.LoginRouteDestination
import io.foundy.feature.home.ui.destinations.HomeRouteDestination
import io.foundy.feature.welcome.ui.destinations.WelcomeRouteDestination
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule(order = 0)
    val dispatcher = MainDispatcherRule()

    private val authRepository = FakeAuthRepository()

    private lateinit var viewModel: MainViewModel

    @Test
    fun `should startDestination is Login when not signed in`() = runTest {
        initViewModel()
        authRepository.emitState(AuthState.NotSignedIn)

        assertEquals(LoginRouteDestination, viewModel.startDestination)
    }

    @Test
    fun `should startDestination is Home when currentUserId and init info exist`() = runTest {
        initViewModel()
        authRepository.emitState(AuthState.SignedIn(currentUserId = "id", existsInitInfo = true))

        assertEquals(HomeRouteDestination, viewModel.startDestination)
    }

    @Test
    fun `should startDestination is Welcome when currentUserId exists but init info not`() =
        runTest {
            initViewModel()
            authRepository.emitState(
                AuthState.SignedIn(
                    currentUserId = "id",
                    existsInitInfo = false
                )
            )

            assertEquals(WelcomeRouteDestination, viewModel.startDestination)
        }

    @Test
    fun `should startDestination is Login when there is error`() =
        runTest {
            initViewModel()
            authRepository.emitState(AuthState.Error)

            assertEquals(LoginRouteDestination, viewModel.startDestination)
        }

    private fun initViewModel() {
        viewModel = MainViewModel(
            getAuthStateStreamUseCase = GetAuthStateStreamUseCase(authRepository = authRepository)
        )
    }
}
