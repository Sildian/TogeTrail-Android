package com.sildian.apps.togetrail.event.model.support

import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.NullPointerException

/*************************************************************************************************
 * This viewModel observes a single Event related data
 ************************************************************************************************/

class EventViewModel : BaseObservableViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Messages**/
        private const val EXCEPTION_MESSAGE_SAVE_NULL="Cannot save a null event"
        private const val EXCEPTION_MESSAGE_ATTACH_NULL="Cannot attach / detach a trail to a null event"
        private const val EXCEPTION_MESSAGE_REGISTER_NULL="Cannot register / unregister a hiker to a null event"
    }

    /***************************************Data*************************************************/

    var event: Event?=null ; private set            //The event
    val attachedTrails= arrayListOf<Trail>()        //The list of attached trails (useful only when the event has no id yet)

    /************************************Data monitoring*****************************************/

    /**Initializes a new event**/

    fun initNewEvent(){
        this.event=Event()
        notifyDataChanged()
    }

    /**
     * Checks that the current user is the event's author
     * @return a boolean
     */

    fun currentUserIsAuthor():Boolean =
        AuthRepository.getCurrentUser()?.uid == this.event?.authorId

    /**
     * Loads an event from the database in real time
     * @param eventId : the event's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadEventFromDatabaseRealTime(eventId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        this.queryRegistration?.remove()
        this.queryRegistration = EventRepository.getEventReference(eventId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    event = snapshot.toObject(Event::class.java)
                    notifyDataChanged()
                    successCallback?.invoke()
                }
                else if(e!=null){
                    failureCallback?.invoke(e)
                }
            }
    }

    /**
     * Loads an event from the database
     * @param eventId : the event's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadEventFromDatabase(eventId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try {
                val deferredEvent = async { EventRepository.getEvent(eventId) }
                event = deferredEvent.await()
                notifyDataChanged()
                successCallback?.invoke()
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Saves the event within the database
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun saveEventInDatabase(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try{
                if(event!=null){

                    /*If the event is new...*/

                    if (event?.id == null) {

                        /*Creates the event and its attached trails*/

                        event?.authorId = AuthRepository.getCurrentUser()?.uid
                        val deferredEventId= async { EventRepository.addEvent(event!!) }
                        event!!.id=deferredEventId.await()
                        launch { EventRepository.updateEvent(event!!) }.join()
                        attachedTrails.forEach { trail ->
                            launch { EventRepository.updateEventAttachedTrail(event!!.id!!, trail) }.join()
                        }

                        /*Updates the author's profile*/

                        event?.authorId?.let { authorId ->
                            val deferredHiker = async { HikerRepository.getHiker(authorId) }
                            val hiker = deferredHiker.await()
                            hiker!!.nbEventsCreated++
                            launch { HikerRepository.updateHiker(hiker) }.join()

                            /*And creates an history item*/

                            val historyItem = HikerHistoryItem(
                                HikerHistoryType.EVENT_CREATED,
                                event!!.creationDate,
                                event!!.id!!,
                                event!!.name!!,
                                event!!.meetingPoint.toString(),
                                event!!.mainPhotoUrl
                            )
                            launch { HikerRepository.addHikerHistoryItem(hiker.id, historyItem) }.join()
                        }
                        successCallback?.invoke()
                    } else {
                        launch { EventRepository.updateEvent(event!!) }.join()
                        successCallback?.invoke()
                    }
                }
                else{
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_SAVE_NULL))
                }
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Attaches a trail to the event
     * @param trail : the trail to attach
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun attachTrail(trail:Trail, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try{
                if(event!=null){
                    launch { EventRepository.updateEventAttachedTrail(event!!.id!!, trail) }.join()
                }
                else{
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_ATTACH_NULL))
                }
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Detaches a trail from the event
     * @param trail : the trail to detach
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun detachTrail(trail:Trail, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try{
                if(event!=null){
                    launch { EventRepository.deleteEventAttachedTrail(event!!.id!!, trail.id!!) }.join()
                }
                else{
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_ATTACH_NULL))
                }
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Registers the current user to the event
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun registerUserToEvent(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try{

                /*Gets the current user and the related Hiker info*/

                val user = AuthRepository.getCurrentUser()
                user?.let { usr ->
                    val deferredHiker = async { HikerRepository.getHiker(usr.uid) }
                    val hiker = deferredHiker.await()

                    /*If both hiker and event are not null...*/

                    if(hiker!=null && event!=null) {

                        /*Registers the hiker to the event*/

                        hiker.nbEventsAttended++
                        event!!.nbHikersRegistered++
                        launch { HikerRepository.updateHiker(hiker) }
                        launch { EventRepository.updateEvent(event!!) }
                        launch { HikerRepository.updateHikerAttendedEvent(hiker.id, event!!) }
                        launch { EventRepository.updateEventRegisteredHiker(event!!.id!!, hiker) }

                        /*Then creates an hiker history item*/

                        val historyItem = HikerHistoryItem(
                            HikerHistoryType.EVENT_ATTENDED,
                            Date(),
                            event!!.id,
                            event!!.name,
                            event!!.meetingPoint.toString(),
                            event!!.mainPhotoUrl
                        )

                        launch { HikerRepository.addHikerHistoryItem(hiker.id, historyItem) }

                        successCallback?.invoke()
                    }
                    else{
                        failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_REGISTER_NULL))
                    }
                }
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Unregisters the current user from the event
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun unregisterUserToEvent(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try{

                /*Gets the current user and the related Hiker info*/

                val user = AuthRepository.getCurrentUser()
                user?.let { usr ->
                    val deferredHiker = async { HikerRepository.getHiker(usr.uid) }
                    val hiker = deferredHiker.await()

                    /*If both hiker and event are not null...*/

                    if(hiker!=null && event!=null) {

                        /*Unregisters the hiker from the event*/

                        hiker.nbEventsAttended--
                        event!!.nbHikersRegistered--
                        launch { HikerRepository.updateHiker(hiker) }
                        launch { EventRepository.updateEvent(event!!) }
                        launch { HikerRepository.deleteHikerAttendedEvent(hiker.id, event!!.id!!) }
                        launch { EventRepository.deleteEventRegisteredHiker(event!!.id!!, hiker.id) }

                        //TODO find a way to delete the related hiker history

                        successCallback?.invoke()
                    }
                    else{
                        failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_REGISTER_NULL))
                    }
                }
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }
}