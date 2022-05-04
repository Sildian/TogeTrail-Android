package com.sildian.apps.togetrail.dataRequestTestSupport

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.event.data.core.Event
import com.sildian.apps.togetrail.hiker.data.core.Hiker
import com.sildian.apps.togetrail.chat.data.core.Message
import com.sildian.apps.togetrail.event.data.source.EventRepository
import com.sildian.apps.togetrail.trail.data.core.Trail
import org.mockito.Mockito

/*************************************************************************************************
 * Fake repository for Event
 ************************************************************************************************/

class FakeEventRepository: EventRepository {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE EventRepository : Request failure"
    }

    override fun getEventReference(eventId: String): DocumentReference =
        Mockito.mock(DocumentReference::class.java)

    override suspend fun getEvent(eventId:String): Event? {
        println("FAKE EventRepository : Get event")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.events.firstOrNull { it.id == eventId }
    }

    override suspend fun addEvent(event:Event): String? {
        println("FAKE EventRepository : Add event")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        event.id = "ENEW"
        FirebaseSimulator.events.add(event)
        return event.id
    }

    override suspend fun updateEvent(event:Event) {
        println("FAKE EventRepository : Update event")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.events.firstOrNull { it.id == event.id }?.let { existingEvent ->
            FirebaseSimulator.events.remove(existingEvent)
            FirebaseSimulator.events.add(event)
        }
    }

    override suspend fun updateEventAttachedTrail(eventId:String, trail: Trail) {
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

    override suspend fun deleteEventAttachedTrail(eventId:String, trailId:String) {
        println("FAKE EventRepository : Delete attached trail")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.eventAttachedTrails[eventId]?.let { trails ->
            trails.removeIf { it.id == trailId }
        }
    }

    override suspend fun updateEventRegisteredHiker(eventId:String, hiker: Hiker) {
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

    override suspend fun deleteEventRegisteredHiker(eventId:String, hikerId:String) {
        println("FAKE EventRepository : Delete registered hiker")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.eventRegisteredHikers[eventId]?.let { hikers ->
            hikers.removeIf { it.id == hikerId }
        }
    }

    override suspend fun createOrUpdateEventMessage(eventId:String, message: Message) {
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

    override suspend fun deleteEventMessage(eventId:String, messageId: String) {
        println("FAKE EventRepository : Delete message")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.eventMessages[eventId]?.let { messages ->
            messages.removeIf { it.id == messageId }
        }
    }
}