package com.sildian.apps.togetrail.event.data.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FakeEventRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.event.data.models.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class EventLoadDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_loadEvent_then_checkEventIsNull() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A"))
            val event: Event? = try {
                val dataRequest = EventLoadDataRequest(dispatcher,"EA",FakeEventRepository())
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
                dataRequest.data
            } catch (e: FirebaseException) {
                println(e.message)
                null
            }
            assertNull(event)
        }
    }

    @Test
    fun given_eventId_when_loadEventFromDatabase_then_checkEventName() {
        runBlocking {
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A"))
            val dataRequest = EventLoadDataRequest(dispatcher, "EA", FakeEventRepository())
            dataRequest.execute()
            val event = dataRequest.data
            assertEquals("Event A", event?.name)
        }
    }
}