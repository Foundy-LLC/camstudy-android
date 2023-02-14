package io.foundy.room.data

import io.foundy.room.data.model.OnCreated
import io.foundy.room.data.model.RoomEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoomSocketServiceTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun onCreatedEvent() = runTest {
        val service = FakeRoomService()
        var event: RoomEvent? = null
        val job = launch(testDispatcher) {
            service.event.collectLatest {
                event = it
            }
        }

        assertEquals(OnCreated, event)

        job.cancel()
    }
}
