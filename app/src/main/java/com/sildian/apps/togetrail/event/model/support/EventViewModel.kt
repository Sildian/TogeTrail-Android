package com.sildian.apps.togetrail.event.model.support

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.CoroutineExceptionHandler
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

        /**Logs**/
        private const val TAG = "EventViewModel"

        /**Messages**/
        private const val EXCEPTION_MESSAGE_SAVE_NULL="Cannot save a null event"
        private const val EXCEPTION_MESSAGE_ATTACH_NULL="Cannot attach / detach a trail to a null event"
        private const val EXCEPTION_MESSAGE_REGISTER_NULL="Cannot register / unregister a hiker to a null event"
    }

    /***********************************Exception handler***************************************/

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
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
                    Log.d(TAG, "Successfully loaded event from database")
                    successCallback?.invoke()
                }
                else if(e!=null){
                    Log.e(TAG, "Failed to load event from database : ${e.message}")
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
        viewModelScope.launch(this.exceptionHandler) {
            try {
                val deferredEvent = async { EventRepository.getEvent(eventId) }
                event = deferredEvent.await()
                notifyDataChanged()
                Log.d(TAG, "Successfully loaded event from database")
                successCallback?.invoke()
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to load event from database : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }

    /**
     * Saves the event within the database
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun saveEventInDatabase(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try{
                if(event!=null){

                    /*If the event is new...*/

                    if (event?.id == null) {

                        /*Creates the event and its attached trails*/

                        event?.authorId = AuthRepository.getCurrentUser()?.uid
                        val deferredEventId= async { EventRepository.addEvent(event!!) }
                        event?.id=deferredEventId.await()
                        launch { EventRepository.updateEvent(event!!) }.join()
                        attachedTrails.forEach { trail ->
                            launch { EventRepository.updateEventAttachedTrail(event!!.id!!, trail) }.join()
                        }

                        /*Updates the author's profile*/

                        AuthRepository.getCurrentUserProfile()?.let { hiker ->
                            hiker.nbEventsCreated++
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
                        Log.d(TAG, "Successfully saved event in database")
                        successCallback?.invoke()

                    } else {

                        /*If the event is not new, just updates it*/

                        launch { EventRepository.updateEvent(event!!) }.join()
                        Log.d(TAG, "Successfully saved event in database")
                        successCallback?.invoke()
                    }
                }
                else{
                    Log.e(TAG, "Failed to save event in database : $EXCEPTION_MESSAGE_SAVE_NULL")
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_SAVE_NULL))
                }
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to save event in database : ${e.message}")
                failureCallback?.invoke(e)
                throw e
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
        viewModelScope.launch(this.exceptionHandler) {
            try{
                if(event!=null){
                    launch { EventRepository.updateEventAttachedTrail(event!!.id!!, trail) }.join()
                    Log.d(TAG, "Successfully attached new trail to the event")
                    successCallback?.invoke()
                }
                else{
                    Log.e(TAG, "Failed to attach new trail to the event : $EXCEPTION_MESSAGE_ATTACH_NULL")
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_ATTACH_NULL))
                }
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to attach new trail to the event : ${e.message}")
                failureCallback?.invoke(e)
                throw e
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
        viewModelScope.launch(this.exceptionHandler) {
            try{
                if(event!=null){
                    launch { EventRepository.deleteEventAttachedTrail(event!!.id!!, trail.id!!) }.join()
                    Log.d(TAG, "Successfully detached trail from the event")
                    successCallback?.invoke()
                }
                else{
                    Log.e(TAG, "Failed to detach trail from the event : $EXCEPTION_MESSAGE_ATTACH_NULL")
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_ATTACH_NULL))
                }
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to detach trail from the event : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }

    /**
     * Registers the current user to the event
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun registerUserToEvent(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try {

                /*Gets the current user and the related Hiker info*/

                val hiker = AuthRepository.getCurrentUserProfile()

                /*If both hiker and event are not null...*/

                if (hiker != null && event != null) {

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

                    Log.d(TAG, "Successfully registered new user to the event")
                    successCallback?.invoke()
                } else {
                    Log.e(TAG, "Failed to register new user to the event : $EXCEPTION_MESSAGE_REGISTER_NULL")
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_REGISTER_NULL))
                }
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to register new user to the event : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }

    /**
     * Unregisters the current user from the event
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun unregisterUserToEvent(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try {

                /*Gets the current user and the related Hiker info*/

                val hiker = AuthRepository.getCurrentUserProfile()

                /*If both hiker and event are not null...*/

                if (hiker != null && event != null) {

                    /*Unregisters the hiker from the event*/

                    hiker.nbEventsAttended--
                    event!!.nbHikersRegistered--
                    launch { HikerRepository.updateHiker(hiker) }
                    launch { EventRepository.updateEvent(event!!) }
                    launch { HikerRepository.deleteHikerAttendedEvent(hiker.id, event!!.id!!) }
                    launch { EventRepository.deleteEventRegisteredHiker(event!!.id!!, hiker.id) }

                    //TODO find a way to delete the related hiker history

                    Log.d(TAG, "Successfully unregistered user from the event")
                    successCallback?.invoke()
                } else {
                    Log.e(TAG, "Failed to unregister user from the event : $EXCEPTION_MESSAGE_REGISTER_NULL")
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_REGISTER_NULL))
                }
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to unregister user from the event : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }
}