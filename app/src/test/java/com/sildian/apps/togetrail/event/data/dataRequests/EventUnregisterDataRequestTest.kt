package com.sildian.apps.togetrail.event.data.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.firebaseTestSupport.FakeEventRepository
import com.sildian.apps.togetrail.firebaseTestSupport.FakeHikerRepository
import com.sildian.apps.togetrail.firebaseTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryType
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class EventUnregisterDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_unregisterFromEvent_then_checkHikerIsNotUnregistered() {
        runBlocking {
            FirebaseSimulator.requestShouldFail = true
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", nbEventsAttended = 1))
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A", nbHikersRegistered = 1))
            FirebaseSimulator.hikerAttendedEvents["HA"] = arrayListOf(FirebaseSimulator.events[0].copy())
            FirebaseSimulator.eventRegisteredHikers["EA"] = arrayListOf(FirebaseSimulator.hikers[0].copy())
            FirebaseSimulator.hikerHistoryItems["HA"] = arrayListOf(HikerHistoryItem(type = HikerHistoryType.EVENT_ATTENDED, itemId = "EA"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0].copy()
            try {
                EventUnregisterDataRequest(
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
            assertEquals(1, event.nbHikersRegistered)
            assertEquals(false, FirebaseSimulator.eventRegisteredHikers["EA"]?.isEmpty())
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(1, hiker.nbEventsAttended)
            assertEquals(false, FirebaseSimulator.hikerAttendedEvents["HA"]?.isEmpty())
            assertEquals(false, FirebaseSimulator.hikerHistoryItems["HA"]?.isEmpty())
        }
    }

    @Test
    fun given_nullHiker_when_unregisterFromEvent_then_checkHikerIsNotUnregistered() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", nbEventsAttended = 1))
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A", nbHikersRegistered = 1))
            FirebaseSimulator.hikerAttendedEvents["HA"] = arrayListOf(FirebaseSimulator.events[0].copy())
            FirebaseSimulator.eventRegisteredHikers["EA"] = arrayListOf(FirebaseSimulator.hikers[0].copy())
            FirebaseSimulator.hikerHistoryItems["HA"] = arrayListOf(HikerHistoryItem(type = HikerHistoryType.EVENT_ATTENDED, itemId = "EA"))
            try {
                EventUnregisterDataRequest(
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
            assertEquals(1, event.nbHikersRegistered)
            assertEquals(false, FirebaseSimulator.eventRegisteredHikers["EA"]?.isEmpty())
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(1, hiker.nbEventsAttended)
            assertEquals(false, FirebaseSimulator.hikerAttendedEvents["HA"]?.isEmpty())
            assertEquals(false, FirebaseSimulator.hikerHistoryItems["HA"]?.isEmpty())
        }
    }

    @Test
    fun given_nullEvent_when_unregisterFromEvent_then_checkHikerIsNotUnregistered() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", nbEventsAttended = 1))
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A", nbHikersRegistered = 1))
            FirebaseSimulator.hikerAttendedEvents["HA"] = arrayListOf(FirebaseSimulator.events[0].copy())
            FirebaseSimulator.eventRegisteredHikers["EA"] = arrayListOf(FirebaseSimulator.hikers[0].copy())
            FirebaseSimulator.hikerHistoryItems["HA"] = arrayListOf(HikerHistoryItem(type = HikerHistoryType.EVENT_ATTENDED, itemId = "EA"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0].copy()
            try {
                EventUnregisterDataRequest(
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
            assertEquals(1, event.nbHikersRegistered)
            assertEquals(false, FirebaseSimulator.eventRegisteredHikers["EA"]?.isEmpty())
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(1, hiker.nbEventsAttended)
            assertEquals(false, FirebaseSimulator.hikerAttendedEvents["HA"]?.isEmpty())
            assertEquals(false, FirebaseSimulator.hikerHistoryItems["HA"]?.isEmpty())
        }
    }

    @Test
    fun given_eventWithoutId_when_registerToEvent_then_checkHikerIsNotRegistered() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", nbEventsAttended = 1))
            FirebaseSimulator.events.add(Event(name = "Event A", nbHikersRegistered = 1))
            FirebaseSimulator.hikerAttendedEvents["HA"] = arrayListOf(FirebaseSimulator.events[0].copy())
            FirebaseSimulator.eventRegisteredHikers["EA"] = arrayListOf(FirebaseSimulator.hikers[0].copy())
            FirebaseSimulator.hikerHistoryItems["HA"] = arrayListOf(HikerHistoryItem(type = HikerHistoryType.EVENT_ATTENDED, itemId = "EA"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0].copy()
            try {
                EventUnregisterDataRequest(
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
            assertEquals(1, event.nbHikersRegistered)
            assertEquals(false, FirebaseSimulator.eventRegisteredHikers["EA"]?.isEmpty())
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(1, hiker.nbEventsAttended)
            assertEquals(false, FirebaseSimulator.hikerAttendedEvents["HA"]?.isEmpty())
            assertEquals(false, FirebaseSimulator.hikerHistoryItems["HA"]?.isEmpty())
        }
    }

    @Test
    fun given_event_when_unregisterFromEvent_then_checkHikerIsUnregistered() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A", nbEventsAttended = 1))
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A", nbHikersRegistered = 1))
            FirebaseSimulator.hikerAttendedEvents["HA"] = arrayListOf(FirebaseSimulator.events[0].copy())
            FirebaseSimulator.eventRegisteredHikers["EA"] = arrayListOf(FirebaseSimulator.hikers[0].copy())
            FirebaseSimulator.hikerHistoryItems["HA"] = arrayListOf(HikerHistoryItem(type = HikerHistoryType.EVENT_ATTENDED, itemId = "EA"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers[0].copy()
            EventUnregisterDataRequest(
                dispatcher,
                FirebaseSimulator.events[0].copy(),
                FakeEventRepository(),
                FakeHikerRepository()
            ).execute()
            val event = FirebaseSimulator.events[0]
            assertEquals(0, event.nbHikersRegistered)
            assertEquals(true, FirebaseSimulator.eventRegisteredHikers["EA"]?.isEmpty())
            val hiker = FirebaseSimulator.hikers[0]
            assertEquals(0, hiker.nbEventsAttended)
            assertEquals(true, FirebaseSimulator.hikerAttendedEvents["HA"]?.isEmpty())
            assertEquals(true, FirebaseSimulator.hikerHistoryItems["HA"]?.isEmpty())
        }
    }
}