package com.sildian.apps.togetrail.event.model.support

import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.event.model.core.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Event related data
 ************************************************************************************************/

class EventViewModel : BaseObservableViewModel() {

    /***************************************Data*************************************************/

    var event: Event?=null

    /************************************Data monitoring*****************************************/

    /**
     * Loads an event from the database in real time
     * @param eventId : the event's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadEventFromDatabaseRealTime(eventId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
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
}