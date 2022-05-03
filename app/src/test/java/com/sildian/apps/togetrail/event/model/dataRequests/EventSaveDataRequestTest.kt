package com.sildian.apps.togetrail.event.model.dataRequests

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.dataRequestTestSupport.BaseDataRequestTest
import com.sildian.apps.togetrail.dataRequestTestSupport.FakeEventRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.FakeHikerRepository
import com.sildian.apps.togetrail.dataRequestTestSupport.FirebaseSimulator
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class EventSaveDataRequestTest: BaseDataRequestTest() {

    @Test
    fun given_requestFailure_when_saveEvent_then_checkEventIsNotSaved() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            FirebaseSimulator.requestShouldFail = true
            try {
                val event = Event(id = "EA", name = "Event A")
                val dataRequest = EventSaveDataRequest(
                    dispatcher,
                    event,
                    FakeHikerRepository(),
                    FakeEventRepository()
                )
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: FirebaseException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.events.isEmpty())
            assertEquals(0, CurrentHikerInfo.currentHiker?.nbEventsCreated)
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_nullHiker_when_saveEvent_then_checkEventIsNotSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = null
            try {
                val event = Event(id = "EA", name = "Event A")
                val dataRequest = EventSaveDataRequest(
                    dispatcher,
                    event,
                    FakeHikerRepository(),
                    FakeEventRepository()
                )
                dataRequest.execute()
                assertEquals("TRUE", "FALSE")
            } catch (e: NullPointerException) {
                println(e.message)
            }
            assertTrue(FirebaseSimulator.events.isEmpty())
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_nullEvent_when_saveEvent_then_checkEventIsNotSaved() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            launch {
                try {
                    val dataRequest = EventSaveDataRequest(
                        dispatcher,
                        null,
                        FakeHikerRepository(),
                        FakeEventRepository()
                    )
                    dataRequest.execute()
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertTrue(FirebaseSimulator.events.isEmpty())
            assertEquals(0, CurrentHikerInfo.currentHiker?.nbEventsCreated)
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_existingEvent_when_saveEvent_then_checkEventIsSaved() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            FirebaseSimulator.events.add(Event(id = "EA", name = "Event A"))
            val event = Event(id = "EA", name = "Event A", description = "Super event")
            val dataRequest = EventSaveDataRequest(
                dispatcher,
                event,
                FakeHikerRepository(),
                FakeEventRepository()
            )
            dataRequest.execute()
            val eventFromDb = FirebaseSimulator.events.firstOrNull { it.id == "EA" }
            assertNotNull(eventFromDb)
            assertEquals(1, FirebaseSimulator.events.size)
            assertEquals("Event A", eventFromDb?.name)
            assertEquals("Super event", eventFromDb?.description)
            assertEquals(0, CurrentHikerInfo.currentHiker?.nbEventsCreated)
            assertTrue(FirebaseSimulator.hikerHistoryItems.isEmpty())
        }
    }

    @Test
    fun given_newEvent_when_saveEvent_then_checkEventIsSaved() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            val event = Event(name = "Event New", description = "Super event")
            val dataRequest = EventSaveDataRequest(
                dispatcher,
                event,
                FakeHikerRepository(),
                FakeEventRepository()
            )
            dataRequest.execute()
            val eventFromDb = FirebaseSimulator.events.firstOrNull { it.id == "ENEW" }
            assertNotNull(eventFromDb)
            assertEquals(1, FirebaseSimulator.events.size)
            assertEquals("Event New", eventFromDb?.name)
            assertEquals("Super event", eventFromDb?.description)
            assertEquals(1, CurrentHikerInfo.currentHiker?.nbEventsCreated)
            val historyItem = FirebaseSimulator.hikerHistoryItems["HA"]?.get(0)
            assertEquals(HikerHistoryType.EVENT_CREATED, historyItem?.type)
        }
    }

    @Test
    fun given_newEventWithTrails_when_saveEvent_then_checkEventIsSavedWithTrails() {
        runBlocking {
            FirebaseSimulator.hikers.add(Hiker(id = "HA", name = "Hiker A"))
            CurrentHikerInfo.currentHiker = FirebaseSimulator.hikers.first()
            FirebaseSimulator.trails.add(Trail(id = "TA", name = "Trail A"))
            FirebaseSimulator.trails.add(Trail(id = "TB", name = "Trail B"))
            val event = Event(name = "Event New", description = "Super event")
            val trails = arrayListOf(FirebaseSimulator.trails[0], FirebaseSimulator.trails[1])
            val dataRequest = EventSaveDataRequest(
                dispatcher,
                event,
                FakeHikerRepository(),
                FakeEventRepository()
            )
                .addAttachedTrails(trails)
            dataRequest.execute()
            val eventFromDb = FirebaseSimulator.events.firstOrNull { it.id == "ENEW" }
            assertNotNull(eventFromDb)
            assertEquals(1, FirebaseSimulator.events.size)
            assertEquals("Event New", eventFromDb?.name)
            assertEquals("Super event", eventFromDb?.description)
            assertEquals(1, CurrentHikerInfo.currentHiker?.nbEventsCreated)
            assertEquals(2, FirebaseSimulator.eventAttachedTrails["ENEW"]?.size)
            assertEquals("Trail A", FirebaseSimulator.eventAttachedTrails["ENEW"]?.get(0)?.name)
            assertEquals("Trail B", FirebaseSimulator.eventAttachedTrails["ENEW"]?.get(1)?.name)
            val historyItem = FirebaseSimulator.hikerHistoryItems["HA"]?.get(0)
            assertEquals(HikerHistoryType.EVENT_CREATED, historyItem?.type)
        }
    }
}