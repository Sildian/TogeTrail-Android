package com.sildian.apps.togetrail.event.model.support

import com.sildian.apps.togetrail.BaseDataRequesterTest
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EventDataRequesterTest: BaseDataRequesterTest() {

    private lateinit var eventDataRequester: EventDataRequester

    @Before
    fun init() {
        eventDataRequester = EventDataRequester()
    }

    @Test
    fun given_eventId_when_loadEventFromDatabase_then_checkEventName() {
        runBlocking {
            val event = async { eventDataRequester.loadEventFromDatabase(EVENT_ID) }.await()
            assertEquals(EVENT_NAME, event?.name)
        }
    }

    @Test
    fun given_nullHiker_when_saveEventInDatabase_then_checkEventIsNotSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = null
            launch {
                try {
                    eventDataRequester.saveEventInDatabase(getEventSample(), null)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {

                }
            }.join()
            assertFalse(isEventAdded)
            assertFalse(isEventUpdated)
            assertFalse(isEventHasTrailAttached)
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_nullEvent_when_saveEventInDatabase_then_checkEventIsNotSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch {
                try {
                    eventDataRequester.saveEventInDatabase(null, null)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {

                }
            }.join()
            assertFalse(isEventAdded)
            assertFalse(isEventUpdated)
            assertFalse(isEventHasTrailAttached)
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_existingEvent_when_saveEventInDatabase_then_checkEventIsSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch { eventDataRequester.saveEventInDatabase(getEventSample(), null) }.join()
            assertFalse(isEventAdded)
            assertTrue(isEventUpdated)
            assertFalse(isEventHasTrailAttached)
            assertFalse(isHikerUpdated)
            assertFalse(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_newEvent_when_saveEventInDatabase_then_checkEventIsSavedAndHikerInfoAreUpdated() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            val event = getEventSample()
            event?.id = null
            launch { eventDataRequester.saveEventInDatabase(event, null) }.join()
            assertTrue(isEventAdded)
            assertTrue(isEventUpdated)
            assertFalse(isEventHasTrailAttached)
            assertTrue(isHikerUpdated)
            assertTrue(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_newEventWithTrails_when_saveEventInDatabase_then_checkEventIsSavedAndTrailsAreAttachedAndHikerInfoAreUpdated() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            val event = getEventSample()
            event?.id = null
            val trails: List<Trail> = listOf(getTrailSample()!!)
            launch { eventDataRequester.saveEventInDatabase(event, trails) }.join()
            assertTrue(isEventAdded)
            assertTrue(isEventUpdated)
            assertTrue(isEventHasTrailAttached)
            assertTrue(isHikerUpdated)
            assertTrue(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_nullEvent_when_attachTrail_then_checkTrailIsNotAttached() {
        runBlocking {
            launch {
                try {
                    eventDataRequester.attachTrail(null, getTrailSample()!!)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {

                }
            }.join()
            assertFalse(isEventHasTrailAttached)
        }
    }

    @Test
    fun given_eventWithoutId_when_attachTrail_then_checkTrailIsNotAttached() {
        runBlocking {
            val event = getEventSample()
            event?.id = null
            launch {
                try {
                    eventDataRequester.attachTrail(event, getTrailSample()!!)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {

                }
            }.join()
            assertFalse(isEventHasTrailAttached)
        }
    }

    @Test
    fun given_trailWithoutId_when_attachTrail_then_checkTrailIsNotAttached() {
        runBlocking {
            val trail = getTrailSample()
            trail?.id = null
            launch {
                try {
                    eventDataRequester.attachTrail(getEventSample(), trail!!)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {

                }
            }.join()
            assertFalse(isEventHasTrailAttached)
        }
    }

    @Test
    fun given_eventAndTrail_when_attachTrail_then_checkTrailIsAttached() {
        runBlocking {
            launch { eventDataRequester.attachTrail(getEventSample(), getTrailSample()!!) }.join()
            assertTrue(isEventHasTrailAttached)
        }
    }

    @Test
    fun given_nullEvent_when_detachTrail_then_checkTrailIsNotDetached() {
        runBlocking {
            launch {
                try {
                    eventDataRequester.detachTrail(null, getTrailSample()!!)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {

                }
            }.join()
            assertFalse(isEventHasTrailDetached)
        }
    }

    @Test
    fun given_eventWithoutId_when_detachTrail_then_checkTrailIsNotDetached() {
        runBlocking {
            val event = getEventSample()
            event?.id = null
            launch {
                try {
                    eventDataRequester.detachTrail(event, getTrailSample()!!)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {

                }
            }.join()
            assertFalse(isEventHasTrailDetached)
        }
    }

    @Test
    fun given_trailWithoutId_when_detachTrail_then_checkTrailIsNotDetached() {
        runBlocking {
            val trail = getTrailSample()
            trail?.id = null
            launch {
                try {
                    eventDataRequester.detachTrail(getEventSample(), trail!!)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {

                }
            }.join()
            assertFalse(isEventHasTrailDetached)
        }
    }

    @Test
    fun given_eventAndTrail_when_detachTrail_then_checkTrailIsDetached() {
        runBlocking {
            launch { eventDataRequester.detachTrail(getEventSample(), getTrailSample()!!) }.join()
            assertTrue(isEventHasTrailDetached)
        }
    }
    
    @Test
    fun given_nullHiker_when_registerUserToEvent_then_checkHikerIsNotRegistered() {
        runBlocking { 
            CurrentHikerInfo.currentHiker = null
            launch { 
                try {
                    eventDataRequester.registerUserToEvent(getEventSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    
                }
            }.join()
            assertFalse(isHikerUpdated)
            assertFalse(isEventUpdated)
            assertFalse(isHikerRegisteredToEvent)
            assertFalse(isEventHasHikerRegistered)
            assertFalse(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_nullEvent_when_registerUserToEvent_then_checkHikerIsNotRegistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch {
                try {
                    eventDataRequester.registerUserToEvent(null)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {

                }
            }.join()
            assertFalse(isHikerUpdated)
            assertFalse(isEventUpdated)
            assertFalse(isHikerRegisteredToEvent)
            assertFalse(isEventHasHikerRegistered)
            assertFalse(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_eventWithoutId_when_registerUserToEvent_then_checkHikerIsNotRegistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            val event = getEventSample()
            event?.id = null
            launch {
                try {
                    eventDataRequester.registerUserToEvent(event)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {

                }
            }.join()
            assertFalse(isHikerUpdated)
            assertFalse(isEventUpdated)
            assertFalse(isHikerRegisteredToEvent)
            assertFalse(isEventHasHikerRegistered)
            assertFalse(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_event_when_registerUserToEvent_then_checkHikerIsRegistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch { eventDataRequester.registerUserToEvent(getEventSample()) }.join()
            assertTrue(isHikerUpdated)
            assertTrue(isEventUpdated)
            assertTrue(isHikerRegisteredToEvent)
            assertTrue(isEventHasHikerRegistered)
            assertTrue(isHikerHistoryItemAdded)
        }
    }

    @Test
    fun given_nullHiker_when_unregisterUserFromEvent_then_checkHikerIsNotUnregistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = null
            launch {
                try {
                    eventDataRequester.unregisterUserFromEvent(getEventSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {

                }
            }.join()
            assertFalse(isHikerUpdated)
            assertFalse(isEventUpdated)
            assertFalse(isHikerUnregisteredFromEvent)
            assertFalse(isEventHasHikerUnregistered)
            assertFalse(isHikerHistoryItemDeleted)
        }
    }

    @Test
    fun given_nullEvent_when_unregisterUserFromEvent_then_checkHikerIsNotUnregistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch {
                try {
                    eventDataRequester.unregisterUserFromEvent(null)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {

                }
            }.join()
            assertFalse(isHikerUpdated)
            assertFalse(isEventUpdated)
            assertFalse(isHikerUnregisteredFromEvent)
            assertFalse(isEventHasHikerUnregistered)
            assertFalse(isHikerHistoryItemDeleted)
        }
    }

    @Test
    fun given_eventWithoutId_when_unregisterUserFromEvent_then_checkHikerIsNotUnregistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            val event = getEventSample()
            event?.id = null
            launch {
                try {
                    eventDataRequester.unregisterUserFromEvent(event)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {

                }
            }.join()
            assertFalse(isHikerUpdated)
            assertFalse(isEventUpdated)
            assertFalse(isHikerUnregisteredFromEvent)
            assertFalse(isEventHasHikerUnregistered)
            assertFalse(isHikerHistoryItemDeleted)
        }
    }

    @Test
    fun given_event_when_unregisterUserFromEvent_then_checkHikerIsUnregistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch { eventDataRequester.unregisterUserFromEvent(getEventSample()) }.join()
            assertTrue(isHikerUpdated)
            assertTrue(isEventUpdated)
            assertTrue(isHikerUnregisteredFromEvent)
            assertTrue(isEventHasHikerUnregistered)
            assertTrue(isHikerHistoryItemDeleted)
        }
    }
}