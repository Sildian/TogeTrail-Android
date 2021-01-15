package com.sildian.apps.togetrail

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventRepository
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.trail.model.core.Trail
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(EventRepository::class)
class EventRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE EventRepository : Request failure"
    }

    @Implementation
    suspend fun getEvent(eventId:String): Event? {
        println("FAKE EventRepository : Get event")
        if (!BaseDataRequesterTest.requestShouldFail) {
            return BaseDataRequesterTest.getEventSample()
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun addEvent(event:Event): String? {
        println("FAKE EventRepository : Add event")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isEventAdded = true
            return BaseDataRequesterTest.EVENT_ID
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun updateEvent(event:Event) {
        println("FAKE EventRepository : Update event")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isEventUpdated = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun updateEventAttachedTrail(eventId:String, trail: Trail) {
        println("FAKE EventRepository : Update attached trail")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isEventHasTrailAttached = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteEventAttachedTrail(eventId:String, trailId:String) {
        println("FAKE EventRepository : Delete attached trail")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isEventHasTrailDetached = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun updateEventRegisteredHiker(eventId:String, hiker: Hiker) {
        println("FAKE EventRepository : Update registered hiker")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isEventHasHikerRegistered = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteEventRegisteredHiker(eventId:String, hikerId:String) {
        println("FAKE EventRepository : Delete registered hiker")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isEventHasHikerUnregistered = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun updateEventMessage(eventId:String, message: Message) {
        println("FAKE EventRepository : Update message")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isEventMessageSent = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteEventMessage(eventId:String, messageId: String) {
        println("FAKE EventRepository : Delete message")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isEventMessageDeleted = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }
}