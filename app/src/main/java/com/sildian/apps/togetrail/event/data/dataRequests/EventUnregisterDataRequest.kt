package com.sildian.apps.togetrail.event.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.event.data.source.EventRepository
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryType
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Unregisters the current hiker from an event
 ************************************************************************************************/

class EventUnregisterDataRequest(
    dispatcher: CoroutineDispatcher,
    private val event: Event?,
    private val eventRepository: EventRepository,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest(dispatcher)
{

    override suspend fun run() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            event?.let { event ->
                event.id?.let { eventId ->
                    updateEvent()
                    updateHiker()
                    updateHikerHistory()
                } ?:
                throw IllegalArgumentException("Cannot unregister from an event without id")
            } ?:
            throw NullPointerException("Cannot unregister from a null event")
        } ?:
        throw NullPointerException("Cannot unregister from an event when the current hiker is null")
    }

    private suspend fun updateEvent() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            event?.let { event ->
                event.id?.let { eventId ->
                    if (event.nbHikersRegistered > 0) {
                        event.nbHikersRegistered--
                        eventRepository.updateEvent(event)
                    }
                    eventRepository.deleteEventRegisteredHiker(eventId, hiker.id)
                }
            }
        }
    }

    private suspend fun updateHiker() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            event?.let { event ->
                event.id?.let { eventId ->
                    if (hiker.nbEventsAttended > 0) {
                        hiker.nbEventsAttended--
                        hikerRepository.updateHiker(hiker)
                    }
                    hikerRepository.deleteHikerAttendedEvent(hiker.id, eventId)
                }
            }
        }
    }

    private suspend fun updateHikerHistory() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            event?.let { event ->
                event.id?.let { eventId ->
                    hikerRepository.deleteHikerHistoryItems(
                        hiker.id,
                        HikerHistoryType.EVENT_ATTENDED,
                        eventId
                    )
                }
            }
        }
    }
}