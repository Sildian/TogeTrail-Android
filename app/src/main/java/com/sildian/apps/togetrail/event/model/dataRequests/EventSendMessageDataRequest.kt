package com.sildian.apps.togetrail.event.model.dataRequests

import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventRepository
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo

/*************************************************************************************************
 * Sends a message to an event's chat room
 ************************************************************************************************/

class EventSendMessageDataRequest(
    private val event: Event?,
    private val text: String,
    private val eventRepository: EventRepository
)
    : SpecificDataRequest()
{

    override suspend fun run() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            event?.let { event ->
                event.id?.let { eventId ->
                    text.takeIf { it.isNotEmpty() }?.let {
                        val message = Message(
                            text = text,
                            authorId = hiker.id,
                            authorName = hiker.name,
                            authorPhotoUrl = hiker.photoUrl
                        )
                        eventRepository.createOrUpdateEventMessage(eventId, message)
                    } ?:
                    throw IllegalArgumentException("Cannot send a message with an empty text")
                } ?:
                throw IllegalArgumentException("Cannot send a message to an event without id")
            } ?:
            throw NullPointerException("Cannot send a message to a null event")
        } ?:
        throw NullPointerException("Cannot send a message when the current hiker is null")
    }
}