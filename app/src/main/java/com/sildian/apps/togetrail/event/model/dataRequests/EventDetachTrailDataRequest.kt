package com.sildian.apps.togetrail.event.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventRepository
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Detaches a trail from an event
 ************************************************************************************************/

class EventDetachTrailDataRequest(
    private val event: Event?,
    private val trail: Trail,
    private val eventRepository: EventRepository
):
    SpecificDataRequest()
{

    override suspend fun run() {
        this.event?.let { event ->
            event.id?.let { eventId ->
                this.trail.id?.let { trailId ->
                    this.eventRepository.deleteEventAttachedTrail(eventId, trailId)
                } ?:
                throw IllegalArgumentException("Cannot detach a trail without id")
            } ?:
            throw IllegalArgumentException("Cannot detach any trail from an event without id")
        } ?:
        throw NullPointerException("Cannot detach any trail from a null event")
    }
}