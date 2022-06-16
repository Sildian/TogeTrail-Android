package com.sildian.apps.togetrail.event.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.event.data.source.EventRepository
import com.sildian.apps.togetrail.trail.data.models.Trail
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Attaches a trail to an event
 ************************************************************************************************/

class EventAttachTrailDataRequest(
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
                    this.eventRepository.updateEventAttachedTrail(eventId, trail)
                } ?:
                throw IllegalArgumentException("Cannot attach a trail without id")
            } ?:
            throw IllegalArgumentException("Cannot attach any trail to an event without id")
        } ?:
        throw NullPointerException("Cannot attach any trail to a null event")
    }
}