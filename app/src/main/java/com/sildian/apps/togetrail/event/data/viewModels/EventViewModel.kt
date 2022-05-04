package com.sildian.apps.togetrail.event.data.viewModels

import com.sildian.apps.togetrail.chat.data.core.Message
import com.sildian.apps.togetrail.common.baseViewModels.SingleDataHolder
import com.sildian.apps.togetrail.common.baseViewModels.SingleDataViewModel
import com.sildian.apps.togetrail.common.utils.coroutinesHelpers.CoroutineIODispatcher
import com.sildian.apps.togetrail.event.data.core.Event
import com.sildian.apps.togetrail.event.data.source.EventRepository
import com.sildian.apps.togetrail.event.data.dataRequests.*
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import com.sildian.apps.togetrail.trail.data.core.Trail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes a single Event related data
 ************************************************************************************************/

@HiltViewModel
class EventViewModel @Inject constructor(
    @CoroutineIODispatcher dispatcher: CoroutineDispatcher,
    val hikerRepository: HikerRepository,
    val eventRepository: EventRepository
)
    : SingleDataViewModel<Event>(Event::class.java, dispatcher) {

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
        loadData(EventLoadDataRequest(this.dispatcher, eventId, this.eventRepository))
    }

    fun saveEvent() {
        saveData(EventSaveDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            this.hikerRepository,
            this.eventRepository
        ))
    }

    fun attachTrail(trail: Trail) {
        runSpecificRequest(EventAttachTrailDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            trail,
            this.eventRepository
        ))
    }

    fun detachTrail(trail: Trail) {
        runSpecificRequest(EventDetachTrailDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            trail,
            this.eventRepository
        ))
    }

    fun registerUserToEvent() {
        runSpecificRequest(EventRegisterDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            this.eventRepository,
            this.hikerRepository
        ))
    }

    fun unregisterUserFromEvent() {
        runSpecificRequest(EventUnregisterDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            this.eventRepository,
            this.hikerRepository
        ))
    }

    fun sendMessage(text: String) {
        runSpecificRequest(EventSendMessageDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            text,
            this.eventRepository
        ))
    }

    fun updateMessage(message: Message, newText: String) {
        runSpecificRequest(EventUpdateMessageDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            message,
            newText,
            this.eventRepository
        ))
    }

    fun deleteMessage(message: Message) {
        runSpecificRequest(EventDeleteMessageDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            message,
            this.eventRepository
        ))
    }
}