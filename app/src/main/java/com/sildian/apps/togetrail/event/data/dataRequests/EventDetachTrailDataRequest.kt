package com.sildian.apps.togetrail.event.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.event.data.source.EventRepository
import com.sildian.apps.togetrail.trail.data.models.Trail
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Detaches a trail from an event
 ************************************************************************************************/

class EventDetachTrailDataRequest(
    dispatcher: CoroutineDispatcher,
    private val event: Event?,
    private val trail: Trail,
    private val eventRepository: EventRepository
):
    SpecificDataRequest(dispatcher)
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