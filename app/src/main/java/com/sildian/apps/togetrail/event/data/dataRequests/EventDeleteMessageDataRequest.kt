package com.sildian.apps.togetrail.event.data.dataRequests

import com.sildian.apps.togetrail.chat.data.models.Message
import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.event.data.source.EventRepository
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