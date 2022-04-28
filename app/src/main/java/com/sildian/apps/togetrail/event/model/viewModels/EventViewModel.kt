package com.sildian.apps.togetrail.event.model.viewModels

import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.common.baseViewModels.SingleDataHolder
import com.sildian.apps.togetrail.common.baseViewModels.SingleDataViewModel
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.dataRepository.EventRepository
import com.sildian.apps.togetrail.event.model.dataRequests.*
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes a single Event related data
 ************************************************************************************************/

@HiltViewModel
class EventViewModel @Inject constructor() : SingleDataViewModel<Event>(Event::class.java) {

    /***********************************Repositories*********************************************/

    @Inject lateinit var hikerRepository: HikerRepository
    @Inject lateinit var eventRepository: EventRepository

    /***************************************Extra data*******************************************/

    val attachedTrails = arrayListOf<Trail>()   //Useful only when the event has no id yet

    /************************************Data monitoring*****************************************/

    fun currentUserIsAuthor(): Boolean =
        CurrentHikerInfo.currentHiker?.id == this.mutableData.value?.data?.authorId

    fun initNewEvent() {
        this.mutableData.postValue(SingleDataHolder(Event()))
    }

    fun loadEventRealTime(eventId:String) {
        loadDataRealTime(this.eventRepository.getEventReference(eventId))
    }

    fun loadEvent(eventId:String) {
        loadData(EventLoadDataRequest(eventId, this.eventRepository))
    }

    fun saveEvent() {
        saveData(EventSaveDataRequest(
            this.mutableData.value?.data,
            this.hikerRepository,
            this.eventRepository
        ))
    }

    fun attachTrail(trail: Trail) {
        runSpecificRequest(EventAttachTrailDataRequest(
            this.mutableData.value?.data,
            trail,
            this.eventRepository
        ))
    }

    fun detachTrail(trail: Trail) {
        runSpecificRequest(EventDetachTrailDataRequest(
            this.mutableData.value?.data,
            trail,
            this.eventRepository
        ))
    }

    fun registerUserToEvent() {
        runSpecificRequest(EventRegisterDataRequest(
            this.mutableData.value?.data,
            this.eventRepository,
            this.hikerRepository
        ))
    }

    fun unregisterUserFromEvent() {
        runSpecificRequest(EventUnregisterDataRequest(
            this.mutableData.value?.data,
            this.eventRepository,
            this.hikerRepository
        ))
    }

    fun sendMessage(text: String) {
        runSpecificRequest(EventSendMessageDataRequest(
            this.mutableData.value?.data,
            text,
            this.eventRepository
        ))
    }

    fun updateMessage(message: Message, newText: String) {
        runSpecificRequest(EventUpdateMessageDataRequest(
            this.mutableData.value?.data,
            message,
            newText,
            this.eventRepository
        ))
    }

    fun deleteMessage(message: Message) {
        runSpecificRequest(EventDeleteMessageDataRequest(
            this.mutableData.value?.data,
            message,
            this.eventRepository
        ))
    }
}