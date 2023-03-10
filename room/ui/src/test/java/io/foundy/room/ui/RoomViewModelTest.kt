package io.foundy.room.ui

import io.foundy.core.test.MainDispatcherRule
import io.foundy.room.ui.fake.FakeAuthRepository
import io.foundy.room.ui.fake.FakeRoomService
import io.foundy.room.ui.viewmodel.RoomSideEffect
import io.foundy.room.ui.viewmodel.RoomUiState
import io.foundy.room.ui.viewmodel.RoomViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.RegularTestContainerHost
import org.orbitmvi.orbit.liveTest

@OptIn(ExperimentalCoroutinesApi::class)
class RoomViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val roomService = FakeRoomService()
    private val authRepository = FakeAuthRepository()

    private lateinit var viewModel:
        RegularTestContainerHost<RoomUiState, RoomSideEffect, RoomViewModel>

    @Test
    fun `should join waiting room when success to connect`() = runTest {
        initViewModel()
        viewModel.runOnCreate()

        viewModel.testIntent {
            connect("id")
        }

        val states = viewModel.stateObserver.values
        assertTrue(states[1] is RoomUiState.WaitingRoom.Connected)
    }

    @Test
    fun `should be FailedToConnect when occurs timeout to join waiting room`() = runTest {
        initViewModel()
        roomService.onConnect = { withTimeout(0L) {} }
        viewModel.runOnCreate()

        viewModel.testIntent {
            connect("id")
        }

        val states = viewModel.stateObserver.values
        assertTrue(states[1] is RoomUiState.WaitingRoom.FailedToConnect)
    }

    private fun initViewModel() {
        viewModel = RoomViewModel(
            authRepository = authRepository,
            roomService = roomService
        ).liveTest { dispatcher = mainDispatcherRule.testDispatcher }
    }
}
