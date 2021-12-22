package com.sildian.apps.togetrail.dataRequestTestSupport

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventRepository
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.trail.model.core.Trail
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data request tests
 ************************************************************************************************/

@Implements(EventRepository::class)
class EventRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE EventRepository : Request failure"
    }

    @Implementation
    suspend fun getEvent(eventId:String): Event? {
        println("FAKE EventRepository : Get event")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.events.firstOrNull { it.id == eventId }
    }

    @Implementation
    suspend fun addEvent(event:Event): String? {
        println("FAKE EventRepository : Add event")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        event.id = "ENEW"
        FirebaseSimulator.events.add(event)
        return event.id
    }

    @Implementation
    suspend fun updateEvent(event:Event) {
        println("FAKE EventRepository : Update event")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.events.firstOrNull { it.id == event.id }?.let { existingEvent ->
            FirebaseSimulator.events.remove(existingEvent)
            FirebaseSimulator.events.add(event)
        }
    }

    @Implementation
    suspend fun updateEventAttachedTrail(eventId:String, trail: Trail) {
        println("FAKE EventRepository : Update attached trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.eventAttachedTrails[eventId] == null) {
            FirebaseSimulator.eventAttachedTrails[eventId] = arrayListOf()
        }
        FirebaseSimulator.eventAttachedTrails[eventId]?.let { trails ->
            trails.removeIf { it.id == trail.id }
            trails.add(trail)
        }
    }

    @Implementation
    suspend fun deleteEventAttachedTrail(eventId:String, trailId:String) {
        println("FAKE EventRepository : Delete attached trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.eventAttachedTrails[eventId]?.let { trails ->
            trails.removeIf { it.id == trailId }
        }
    }

    @Implementation
    suspend fun updateEventRegisteredHiker(eventId:String, hiker: Hiker) {
        println("FAKE EventRepository : Update registered hiker")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.eventRegisteredHikers[eventId] == null) {
            FirebaseSimulator.eventRegisteredHikers[eventId] = arrayListOf()
        }
        FirebaseSimulator.eventRegisteredHikers[eventId]?.let { hikers ->
            hikers.removeIf { it.id == hiker.id }
            hikers.add(hiker)
        }
    }

    @Implementation
    suspend fun deleteEventRegisteredHiker(eventId:String, hikerId:String) {
        println("FAKE EventRepository : Delete registered hiker")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.eventRegisteredHikers[eventId]?.let { hikers ->
            hikers.removeIf { it.id == hikerId }
        }
    }

    @Implementation
    suspend fun createOrUpdateEventMessage(eventId:String, message: Message) {
        println("FAKE EventRepository : Create or update message")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        if (FirebaseSimulator.eventMessages[eventId] == null) {
            FirebaseSimulator.eventMessages[eventId] = arrayListOf()
        }
        FirebaseSimulator.eventMessages[eventId]?.let { messages ->
            messages.removeIf { it.id == message.id }
            messages.add(message)
        }
    }

    @Implementation
    suspend fun deleteEventMessage(eventId:String, messageId: String) {
        println("FAKE EventRepository : Delete message")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.eventMessages[eventId]?.let { messages ->
            messages.removeIf { it.id == messageId }
        }
    }
}