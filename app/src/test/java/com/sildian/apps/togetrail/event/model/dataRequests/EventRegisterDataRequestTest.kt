package com.sildian.apps.togetrail.event.model.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.dataRepository.EventRepository
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

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
                    FirebaseSimulator.events[0].copy(),
                    EventRepository(),
                    HikerRepository()
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
                    FirebaseSimulator.events[0].copy(),
                    EventRepository(),
                    HikerRepository()
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
                    null,
                    EventRepository(),
                    HikerRepository()
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
                    FirebaseSimulator.events[0].copy(),
                    EventRepository(),
                    HikerRepository()
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
                FirebaseSimulator.events[0].copy(),
                EventRepository(),
                HikerRepository()
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