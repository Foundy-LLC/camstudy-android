package io.foundy.feature.room.ui

import io.foundy.core.test.MainDispatcherRule
import io.foundy.feature.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.feature.room.ui.fake.FakeAuthRepository
import io.foundy.feature.room.ui.fake.FakeRoomService
import io.foundy.feature.room.ui.media.FakeMediaManager
import io.foundy.feature.room.ui.room.RoomSideEffect
import io.foundy.feature.room.ui.room.RoomUiState
import io.foundy.feature.room.ui.room.RoomViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.RegularTestContainerHost
import org.orbitmvi.orbit.liveTest

@OptIn(ExperimentalCoroutinesApi::class)
class RoomViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var roomService: FakeRoomService
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var mediaManager: FakeMediaManager

    private lateinit var viewModel:
        RegularTestContainerHost<RoomUiState, RoomSideEffect, RoomViewModel>

    @Before
    fun setup() {
        initViewModel()
    }

    @Test
    fun `should join waiting room when success to connect`() = runTest {
        viewModel.testIntent {
            connect("id")
        }

        val states = viewModel.stateObserver.values
        assertTrue(states.last() is RoomUiState.WaitingRoom.Connected)
    }

    @Test
    fun `should be FailedToConnect when occurs timeout to join waiting room`() = runTest {
        roomService.onConnect = { withTimeout(0L) {} }

        viewModel.testIntent {
            connect("id")
        }

        val states = viewModel.stateObserver.values
        assertTrue(states.last() is RoomUiState.WaitingRoom.FailedToConnect)
    }

    @Test
    fun `should call disconnect functions when on cleared`() = runTest {
        viewModel.testIntent {
            onCleared()
        }

        assertTrue(roomService.didDisconnect)
        assertTrue(mediaManager.didDisconnect)
    }

    private fun initViewModel() {
        authRepository = FakeAuthRepository()
        roomService = FakeRoomService()
        mediaManager = FakeMediaManager()
        viewModel = RoomViewModel(
            getCurrentUserIdUseCase = GetCurrentUserIdUseCase(authRepository = authRepository),
            roomService = roomService,
            mediaManager = mediaManager
        ).liveTest { dispatcher = mainDispatcherRule.testDispatcher }
        viewModel.runOnCreate()
    }
}
