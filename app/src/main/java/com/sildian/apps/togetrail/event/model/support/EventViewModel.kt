package com.sildian.apps.togetrail.event.model.support

import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.NullPointerException

/*************************************************************************************************
 * This viewModel observes a single Event related data
 ************************************************************************************************/

class EventViewModel : BaseObservableViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Messages**/
        private const val EXCEPTION_MESSAGE_SAVE_NULL="Cannot save a null event"
    }

    /***************************************Data*************************************************/

    var event: Event?=null ; private set            //The event
    val attachedTrails= arrayListOf<Trail>()        //The list of attached trails (useful only when the event has no id yet)

    /************************************Data monitoring*****************************************/

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
                    if (event?.id == null) {
                        event?.authorId = AuthRepository.getCurrentUser()?.uid
                        val deferredEventId= async { EventRepository.addEvent(event!!) }
                        event!!.id=deferredEventId.await()
                        launch { EventRepository.updateEvent(event!!) }.join()
                        attachedTrails.forEach { trail ->
                            launch { EventRepository.updateEventAttachedTrail(event!!.id!!, trail) }.join()
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
}