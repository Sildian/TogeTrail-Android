package com.sildian.apps.togetrail.event.model.support

import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.common.baseViewModels.SingleDataViewModel
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.dataRequests.*
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * This viewModel observes a single Event related data
 ************************************************************************************************/

class EventViewModel : SingleDataViewModel<Event>(Event::class.java) {

    /***********************************Repositories*********************************************/

    private val hikerRepository = HikerRepository()
    private val eventRepository = EventRepository()

    /***************************************Extra data*******************************************/

    val attachedTrails = arrayListOf<Trail>()   //Useful only when the event has no id yet

    /************************************Data monitoring*****************************************/

    fun currentUserIsAuthor(): Boolean =
        CurrentHikerInfo.currentHiker?.id == this.mutableData.value?.authorId

    fun initNewEvent() {
        this.mutableData.postValue(Event())
    }

    fun loadEventRealTime(eventId:String) {
        loadDataRealTime(this.eventRepository.getEventReference(eventId))
    }

    fun loadEvent(eventId:String) {
        loadData(EventLoadDataRequest(eventId, this.eventRepository))
    }

    fun saveEvent() {
        saveData(EventSaveDataRequest(
            this.mutableData.value,
            this.hikerRepository,
            this.eventRepository
        ))
    }

    fun attachTrail(trail: Trail) {
        runSpecificRequest(EventAttachTrailDataRequest(
            this.mutableData.value,
            trail,
            this.eventRepository
        ))
    }

    fun detachTrail(trail: Trail) {
        runSpecificRequest(EventDetachTrailDataRequest(
            this.mutableData.value,
            trail,
            this.eventRepository
        ))
    }

    fun registerUserToEvent() {
        runSpecificRequest(EventRegisterDataRequest(
            this.mutableData.value,
            this.eventRepository,
            this.hikerRepository
        ))
    }

    fun unregisterUserFromEvent() {
        runSpecificRequest(EventUnregisterDataRequest(
            this.mutableData.value,
            this.eventRepository,
            this.hikerRepository
        ))
    }

    fun sendMessage(text: String) {
        runSpecificRequest(EventSendMessageDataRequest(
            this.mutableData.value,
            text,
            this.eventRepository
        ))
    }

    fun updateMessage(message: Message, newText: String) {
        runSpecificRequest(EventUpdateMessageDataRequest(
            this.mutableData.value,
            message,
            newText,
            this.eventRepository
        ))
    }

    fun deleteMessage(message: Message) {
        runSpecificRequest(EventDeleteMessageDataRequest(
            this.mutableData.value,
            message,
            this.eventRepository
        ))
    }
}