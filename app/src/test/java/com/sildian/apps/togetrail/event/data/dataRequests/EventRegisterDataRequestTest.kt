package com.sildian.apps.togetrail.event.data.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.firebaseTestSupport.FakeEventRepository
import com.sildian.apps.togetrail.firebaseTestSupport.FakeHikerRepository
import com.sildian.apps.togetrail.firebaseTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryType
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class EventRegisterDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_registerToEvent_then_checkHikerIsNotRegistered() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0].copy()
            try {
                EventRegisterDataRequest(
                    dispatcher,
                    FirebaseSimulator.events[0].copy(),
                    FakeEventRepository(),
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: FirebaseException) {
                println(e.message)
            }
            val event = FirebaseSimulator.events[0]
            assertEquals(0, event.nbHikersRegistered)
            assertTrue(FirebaseSimulator.eventRegisteredHikers.isEmpty())
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(0, hiker.nbEventsAttended)
            assertTrue(FirebaseSimulator.hikerAttendedEvents.isEmpty())
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_nullHiker_when_registerToEvent_then_checkHikerIsNotRegistered() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A"))
            try {
                EventRegisterDataRequest(
                    dispatcher,
                    FirebaseSimulator.events[0].copy(),
                    FakeEventRepository(),
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
            val event = FirebaseSimulator.events[0]
            assertEquals(0, event.nbHikersRegistered)
            assertTrue(FirebaseSimulator.eventRegisteredHikers.isEmpty())
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(0, hiker.nbEventsAttended)
            assertTrue(FirebaseSimulator.hikerAttendedEvents.isEmpty())
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_nullEvent_when_registerToEvent_then_checkHikerIsNotRegistered() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0].copy()
            try {
                EventRegisterDataRequest(
                    dispatcher,
                    null,
                    FakeEventRepository(),
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
            val event = FirebaseSimulator.events[0]
            assertEquals(0, event.nbHikersRegistered)
            assertTrue(FirebaseSimulator.eventRegisteredHikers.isEmpty())
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(0, hiker.nbEventsAttended)
            assertTrue(FirebaseSimulator.hikerAttendedEvents.isEmpty())
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_eventWithoutId_when_registerToEvent_then_checkHikerIsNotRegistered() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.events.add(Event(name = "Event A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0].copy()
            try {
                EventRegisterDataRequest(
                    dispatcher,
                    FirebaseSimulator.events[0].copy(),
                    FakeEventRepository(),
                    FakeHikerRepository()
                ).execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
            val event = FirebaseSimulator.events[0]
            assertEquals(0, event.nbHikersRegistered)
            assertTrue(FirebaseSimulator.eventRegisteredHikers.isEmpty())
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(0, hiker.nbEventsAttended)
            assertTrue(FirebaseSimulator.hikerAttendedEvents.isEmpty())
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_event_when_registerToEvent_then_checkHikerIsRegistered() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0].copy()
            EventRegisterDataRequest(
                dispatcher,
                FirebaseSimulator.events[0].copy(),
                FakeEventRepository(),
                FakeHikerRepository()
            ).execute()
            val event = FirebaseSimulator.events[0]
            assertEquals(1, event.nbHikersRegistered)
            assertEquals(1, FirebaseSimulator.eventRegisteredHikers["EA"]?.size)
            assertEquals("HA", FirebaseSimulator.eventRegisteredHikers["EA"]?.get(0)?.id)
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(1, hiker.nbEventsAttended)
            assertEquals(1, FirebaseSimulator.hikerAttendedEvents["HA"]?.size)
            assertEquals("EA", FirebaseSimulator.hikerAttendedEvents["HA"]?.get(0)?.id)
            assertEquals(1, FirebaseSimulator.hikerHistoryItems["HA"]?.size)
            assertEquals(HikerHistoryType.EVENT_ATTENDED, FirebaseSimulator.hikerHistoryItems["HA"]?.get(0)?.type)
        }
    }
}