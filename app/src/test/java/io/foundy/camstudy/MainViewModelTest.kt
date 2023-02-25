package io.foundy.camstudy

import io.foundy.auth.ui.destinations.LoginRouteDestination
import io.foundy.camstudy.fake.FakeAuthRepository
import io.foundy.core.test.MainDispatcherRule
import io.foundy.home.ui.destinations.HomeRouteDestination
import io.foundy.welcome.ui.destinations.WelcomeRouteDestination
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule(order = 0)
    val dispatcher = MainDispatcherRule()

    private val authRepository = FakeAuthRepository()

    private lateinit var viewModel: MainViewModel

    @Test
    fun `should startDestination is Login when currentUserId is null`() = runTest {
        initViewModel()
        authRepository.currentUserIdSharedFlow.emit(null)

        assertEquals(LoginRouteDestination, viewModel.startDestination)
    }

    @Test
    fun `should startDestination is Home when currentUserId and init info exist`() = runTest {
        authRepository.currentUserIdSharedFlow.emit("id")
        authRepository.existsInitInfoTestValue = true
        initViewModel()

        assertEquals(HomeRouteDestination, viewModel.startDestination)
    }

    @Test
    fun `should startDestination is Welcome when currentUserId exists but init info not`() =
        runTest {
            authRepository.currentUserIdSharedFlow.emit("id")
            authRepository.existsInitInfoTestValue = false
            initViewModel()

            assertEquals(WelcomeRouteDestination, viewModel.startDestination)
        }

    @Test
    fun `should startDestination is Login when currentUserId exists but init info is null`() =
        runTest {
            authRepository.currentUserIdSharedFlow.emit("id")
            authRepository.existsInitInfoTestValue = null
            initViewModel()

            assertEquals(LoginRouteDestination, viewModel.startDestination)
        }

    private fun initViewModel() {
        viewModel = MainViewModel(authRepository = authRepository)
    }
}
