package com.sildian.apps.togetrail.event.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.dataRepository.EventRepository
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