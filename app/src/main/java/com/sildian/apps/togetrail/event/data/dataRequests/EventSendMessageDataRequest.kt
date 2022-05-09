package com.sildian.apps.togetrail.event.data.dataRequests

import com.sildian.apps.togetrail.chat.data.models.Message
import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.event.data.source.EventRepository
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Sends a message to an event's chat room
 ************************************************************************************************/

class EventSendMessageDataRequest(
    dispatcher: CoroutineDispatcher,
    private val event: Event?,
    private val text: String,
    private val eventRepository: EventRepository
)
    : SpecificDataRequest(dispatcher)
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