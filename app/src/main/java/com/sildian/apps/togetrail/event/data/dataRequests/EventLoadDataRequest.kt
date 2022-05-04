package com.sildian.apps.togetrail.event.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import com.sildian.apps.togetrail.event.data.core.Event
import com.sildian.apps.togetrail.event.data.source.EventRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Loads an event from the database
 ************************************************************************************************/

class EventLoadDataRequest(
    dispatcher: CoroutineDispatcher,
    private val eventId: String,
    private val eventRepository: EventRepository
    )
    : LoadDataRequest<Event>(dispatcher) {

    override suspend fun load(): Event? =
        eventRepository.getEvent(eventId)
}