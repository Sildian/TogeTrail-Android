package com.sildian.apps.togetrail.event.model.dataRequests

import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.dataRepository.EventRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Deletes a message from an event's chat room
 ************************************************************************************************/

class EventDeleteMessageDataRequest(
    dispatcher: CoroutineDispatcher,
    private val event: Event?,
    private val message: Message,
    private val eventRepository: EventRepository
)
    : SpecificDataRequest(dispatcher)
{

    override suspend fun run() {
        event?.let { event ->
            event.id?.let { eventId ->
                eventRepository.deleteEventMessage(eventId, message.id)
            } ?:
            throw IllegalArgumentException("Cannot delete a message from an event without id")
        } ?:
        throw NullPointerException("Cannot delete a message from a null event")
    }
}