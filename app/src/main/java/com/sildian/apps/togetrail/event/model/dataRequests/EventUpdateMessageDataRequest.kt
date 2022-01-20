package com.sildian.apps.togetrail.event.model.dataRequests

import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.dataRepository.EventRepository

/*************************************************************************************************
 * Updates an existing message within an event's chat room
 ************************************************************************************************/

class EventUpdateMessageDataRequest(
    private val event: Event?,
    private val message: Message,
    private val newText: String,
    private val eventRepository: EventRepository
)
    : SpecificDataRequest() {

    override suspend fun run() {
        event?.let { event ->
            event.id?.let { eventId ->
                newText.takeIf { it.isNotEmpty() }?.let {
                    message.text = newText
                    eventRepository.createOrUpdateEventMessage(eventId, message)
                } ?:
                throw IllegalArgumentException("Cannot update a message with an empty text")
            } ?:
            throw IllegalArgumentException("Cannot update a message within an event without id")
        } ?:
        throw NullPointerException("Cannot update a message within a null event")
    }
}