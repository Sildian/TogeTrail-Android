package com.sildian.apps.togetrail

import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventRepository
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.trail.model.core.Trail
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(EventRepository::class)
class EventRepositoryShadow {

    @Implementation
    suspend fun getEvent(eventId:String): Event? {
        println("FAKE EventRepository : Get event")
        return BaseDataRequesterTest.getEventSample()
    }

    @Implementation
    suspend fun addEvent(event:Event): String? {
        println("FAKE EventRepository : Add event")
        BaseDataRequesterTest.isEventAdded = true
        return BaseDataRequesterTest.EVENT_ID
    }

    @Implementation
    suspend fun updateEvent(event:Event) {
        println("FAKE EventRepository : Update event")
        BaseDataRequesterTest.isEventUpdated = true
    }

    @Implementation
    suspend fun updateEventAttachedTrail(eventId:String, trail: Trail) {
        println("FAKE EventRepository : Update attached trail")
        BaseDataRequesterTest.isEventHasTrailAttached = true
    }

    @Implementation
    suspend fun deleteEventAttachedTrail(eventId:String, trailId:String) {
        println("FAKE EventRepository : Delete attached trail")
        BaseDataRequesterTest.isEventHasTrailDetached = true
    }

    @Implementation
    suspend fun updateEventRegisteredHiker(eventId:String, hiker: Hiker) {
        println("FAKE EventRepository : Update registered hiker")
        BaseDataRequesterTest.isEventHasHikerRegistered = true
    }

    @Implementation
    suspend fun deleteEventRegisteredHiker(eventId:String, hikerId:String) {
        println("FAKE EventRepository : Delete registered hiker")
        BaseDataRequesterTest.isEventHasHikerUnregistered = true
    }
}