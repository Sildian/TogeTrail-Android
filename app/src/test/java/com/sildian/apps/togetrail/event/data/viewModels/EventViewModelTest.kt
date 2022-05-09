package com.sildian.apps.togetrail.event.data.viewModels

import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FakeEventRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.FakeHikerRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.event.data.models.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class EventViewModelTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_loadEvent_then_checkError() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A"))
            val eventViewModel = EventViewModel(
                dispatcher,
                FakeHikerRepository(),
                FakeEventRepository()
            )
            eventViewModel.loadEvent("EA")
            assertNull(eventViewModel.data.value?.data)
            assertNotNull(eventViewModel.data.value?.error)
        }
    }

    @Test
    fun given_eventId_when_loadEventFromDatabase_then_checkEventName() {
        runBlocking {
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A"))
            val eventViewModel = EventViewModel(
                dispatcher,
                FakeHikerRepository(),
                FakeEventRepository()
            )
            eventViewModel.loadEvent("EA")
            assertEquals("Event A", eventViewModel.data.value?.data?.name)
            assertNull(eventViewModel.data.value?.error)
        }
    }
}