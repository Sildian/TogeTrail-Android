package com.sildian.apps.togetrail.event.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SaveDataRequest
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.event.data.source.EventRepository
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryType
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import com.sildian.apps.togetrail.trail.data.models.Trail
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Saves an event within the database
 ************************************************************************************************/

class EventSaveDataRequest(
    dispatcher: CoroutineDispatcher,
    event: Event?,
    private val hikerRepository: HikerRepository,
    private val eventRepository: EventRepository
)
    : SaveDataRequest<Event>(dispatcher, event)
{

    private val attachedTrails = arrayListOf<Trail>()

    fun addAttachedTrails(trails: List<Trail>): EventSaveDataRequest {
        attachedTrails.addAll(trails)
        return this
    }
    
    override suspend fun save() {
        CurrentHikerInfo.currentHiker?.let {
            this.data?.let { event ->
                val isNewEvent = event.id == null
                saveEvent()
                if (isNewEvent) {
                    saveAttachedTrails()
                    updateHikerHistoryWithCreatedEvent()
                }
            }?:
            throw NullPointerException("Cannot perform the requested operation with a null event")
        }?:
        throw NullPointerException("Cannot perform the requested operation while the current hiker is null")
    }

    private suspend fun saveEvent() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            this.data?.let { event ->
                event.authorName = hiker.name
                event.authorPhotoUrl = hiker.photoUrl
                if (event.id == null) {
                    event.authorId = hiker.id
                    event.id = this.eventRepository.addEvent(event)
                }
                this.eventRepository.updateEvent(event)
            }
        }
    }

    private suspend fun saveAttachedTrails() {
        this.data?.id?.let { eventId ->
            this.attachedTrails.forEach { trail ->
                eventRepository.updateEventAttachedTrail(eventId, trail)
            }
        }
    }

    private suspend fun updateHikerHistoryWithCreatedEvent() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            this.data?.let { event ->
                hiker.nbEventsCreated++
                this.hikerRepository.updateHiker(hiker)
                val historyItem = HikerHistoryItem(
                    HikerHistoryType.EVENT_CREATED,
                    event.creationDate,
                    event.id,
                    event.name,
                    event.meetingPoint.toString(),
                    event.mainPhotoUrl
                )
                this.hikerRepository.addHikerHistoryItem(hiker.id, historyItem)
            }
        }
    }
}