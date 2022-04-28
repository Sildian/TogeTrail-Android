package com.sildian.apps.togetrail.event.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.dataRepository.EventRepository
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*

/*************************************************************************************************
 * Registers the current hiker to an event
 ************************************************************************************************/

class EventRegisterDataRequest(
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
                throw IllegalArgumentException("Cannot register to an event without id")
            } ?:
            throw NullPointerException("Cannot register to a null event")
        } ?:
        throw NullPointerException("Cannot register to an event when the current hiker is null")
    }

    private suspend fun updateEvent() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            event?.let { event ->
                event.id?.let { eventId ->
                    event.nbHikersRegistered++
                    eventRepository.updateEvent(event)
                    eventRepository.updateEventRegisteredHiker(eventId, hiker)
                }
            }
        }
    }

    private suspend fun updateHiker() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            event?.let { event ->
                event.id?.let { eventId ->
                    hiker.nbEventsAttended++
                    hikerRepository.updateHiker(hiker)
                    hikerRepository.updateHikerAttendedEvent(hiker.id, event)
                }
            }
        }
    }

    private suspend fun updateHikerHistory() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            event?.let { event ->
                event.id?.let { eventId ->
                    val historyItem = HikerHistoryItem(
                        HikerHistoryType.EVENT_ATTENDED,
                        Date(),
                        event.id,
                        event.name,
                        event.meetingPoint.toString(),
                        event.mainPhotoUrl
                    )
                    hikerRepository.addHikerHistoryItem(hiker.id, historyItem)
                }
            }
        }
    }
}