package com.sildian.apps.togetrail.event.model.support

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.BaseDataRequesterTest
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class EventDataRequesterTest: BaseDataRequesterTest() {

    private val eventDataRequester = EventDataRequester()

    @Test
    fun given_requestFailure_when_loadEventFromDatabase_then_checkEventIsNull() {
        runBlocking {
            requestShouldFail = true
            val event = async {
                try {
                    val event = eventDataRequester.loadEventFromDatabase(EVENT_ID)
                    assertEquals("TRUE", "FALSE")
                    event
                }
                catch (e: FirebaseException) {
                    println(e.message)
                    null
                }
            }.await()
            assertNull(event)
        }
    }

    @Test
    fun given_eventId_when_loadEventFromDatabase_then_checkEventName() {
        runBlocking {
            val event = async { eventDataRequester.loadEventFromDatabase(EVENT_ID) }.await()
            assertEquals(EVENT_NAME, event?.name)
        }
    }

    @Test
    fun given_requestFailure_when_saveEventInDatabase_then_checkEventIsNotSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            requestShouldFail = true
            launch {
                try {
                    eventDataRequester.saveEventInDatabase(getEventSample(), null)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
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
    fun given_nullHiker_when_saveEventInDatabase_then_checkEventIsNotSaved() {
        runBlocking {
            CurrentHikerInfo.currentHiker = null
            launch {
                try {
                    eventDataRequester.saveEventInDatabase(getEventSample(), null)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
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
                    println(e.message)
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
    fun given_requestFailure_when_attachTrail_then_checkTrailIsNotAttached() {
        runBlocking {
            requestShouldFail = true
            launch {
                try {
                    eventDataRequester.attachTrail(getEventSample(), getTrailSample()!!)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventHasTrailAttached)
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
                    println(e.message)
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
                    println(e.message)
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
                    println(e.message)
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
    fun given_requestFailure_when_detachTrail_then_checkTrailIsNotDetached() {
        runBlocking {
            requestShouldFail = true
            launch {
                try {
                    eventDataRequester.detachTrail(getEventSample(), getTrailSample()!!)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventHasTrailDetached)
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
                    println(e.message)
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
                    println(e.message)
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
                    println(e.message)
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
    fun given_requestFailure_when_registerUserToEvent_then_checkHikerIsNotRegistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            requestShouldFail = true
            launch {
                try {
                    eventDataRequester.registerUserToEvent(getEventSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
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
    fun given_nullHiker_when_registerUserToEvent_then_checkHikerIsNotRegistered() {
        runBlocking { 
            CurrentHikerInfo.currentHiker = null
            launch { 
                try {
                    eventDataRequester.registerUserToEvent(getEventSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
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
                    println(e.message)
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
                    println(e.message)
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
    fun given_requestFailure_when_unregisterUserFromEvent_then_checkHikerIsNotUnregistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            requestShouldFail = true
            launch {
                try {
                    eventDataRequester.unregisterUserFromEvent(getEventSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
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
    fun given_nullHiker_when_unregisterUserFromEvent_then_checkHikerIsNotUnregistered() {
        runBlocking {
            CurrentHikerInfo.currentHiker = null
            launch {
                try {
                    eventDataRequester.unregisterUserFromEvent(getEventSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
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
                    println(e.message)
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
                    println(e.message)
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

    @Test
    fun given_requestFailure_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            requestShouldFail = true
            launch {
                try {
                    eventDataRequester.sendMessage(getEventSample(), MESSAGE_TEXT)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageSent)
        }
    }

    @Test
    fun given_nullEvent_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch {
                try {
                    eventDataRequester.sendMessage(null, MESSAGE_TEXT)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageSent)
        }
    }

    @Test
    fun given_NullHiker_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            CurrentHikerInfo.currentHiker = null
            launch {
                try {
                    eventDataRequester.sendMessage(getEventSample(), MESSAGE_TEXT)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageSent)
        }
    }

    @Test
    fun given_eventWithoutId_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            val event = getEventSample()
            event?.id = null
            launch {
                try {
                    eventDataRequester.sendMessage(event, MESSAGE_TEXT)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageSent)
        }
    }

    @Test
    fun given_EmptyText_when_sendMessage_then_checkMessageIsNotSent() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch {
                try {
                    eventDataRequester.sendMessage(getEventSample(), "")
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageSent)
        }
    }

    @Test
    fun given_ValidText_when_sendMessage_then_checkMessageIsSent() {
        runBlocking {
            CurrentHikerInfo.currentHiker = getHikerSample()
            launch { eventDataRequester.sendMessage(getEventSample(), MESSAGE_TEXT) }.join()
            assertTrue(isEventMessageSent)
        }
    }

    @Test
    fun given_requestFailure_when_updateMessage_then_checkMessageIsNotUpdated() {
        runBlocking {
            requestShouldFail = true
            launch {
                try {
                    eventDataRequester.updateMessage(getEventSample(), getMessageSample(), "Hello")
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageUpdated)
        }
    }

    @Test
    fun given_nullEvent_when_updateMessage_then_checkMessageIsNotUpdated() {
        runBlocking {
            launch {
                try {
                    eventDataRequester.updateMessage(null, getMessageSample(), "Hello")
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageUpdated)
        }
    }

    @Test
    fun given_eventWithoutId_when_updatedMessage_then_checkMessageIsNotUpdated() {
        runBlocking {
            val event = getEventSample()
            event?.id = null
            launch {
                try {
                    eventDataRequester.updateMessage(event, getMessageSample(), "Hello")
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageUpdated)
        }
    }

    @Test
    fun given_messageWithoutId_when_updatedMessage_then_checkMessageIsNotUpdated() {
        runBlocking {
            val message = getMessageSample()
            message.id = null
            launch {
                try {
                    eventDataRequester.updateMessage(getEventSample(), message, "Hello")
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageUpdated)
        }
    }

    @Test
    fun given_EmptyText_when_updateMessage_then_checkMessageIsNotUpdated() {
        runBlocking {
            launch {
                try {
                    eventDataRequester.updateMessage(getEventSample(), getMessageSample(), "")
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageUpdated)
        }
    }

    @Test
    fun given_ValidText_when_updateMessage_then_checkMessageIsUpdated() {
        runBlocking {
            launch { eventDataRequester.updateMessage(getEventSample(), getMessageSample(), "Hello") }.join()
            assertTrue(isEventMessageUpdated)
        }
    }

    @Test
    fun given_requestFailure_when_deleteMessage_then_checkMessageIsNotDeleted() {
        runBlocking {
            requestShouldFail = true
            launch {
                try {
                    eventDataRequester.deleteMessage(getEventSample(), getMessageSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: FirebaseException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageDeleted)
        }
    }

    @Test
    fun given_nullEvent_when_deleteMessage_then_checkMessageIsNotDeleted() {
        runBlocking {
            launch {
                try {
                    eventDataRequester.deleteMessage(null, getMessageSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: NullPointerException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageDeleted)
        }
    }

    @Test
    fun given_eventWithoutId_when_deleteMessage_then_checkMessageIsNotDeleted() {
        runBlocking {
            val event = getEventSample()
            event?.id = null
            launch {
                try {
                    eventDataRequester.deleteMessage(event, getMessageSample())
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageDeleted)
        }
    }

    @Test
    fun given_messageWithoutId_when_deleteMessage_then_checkMessageIsNotDeleted() {
        runBlocking {
            val message = getMessageSample()
            message.id = null
            launch {
                try {
                    eventDataRequester.deleteMessage(getEventSample(), message)
                    assertEquals("TRUE", "FALSE")
                }
                catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }.join()
            assertFalse(isEventMessageDeleted)
        }
    }

    @Test
    fun given_ValidMessage_when_deleteMessage_then_checkMessageIsDeleted() {
        runBlocking {
            launch { eventDataRequester.deleteMessage(getEventSample(), getMessageSample()) }.join()
            assertTrue(isEventMessageDeleted)
        }
    }
}