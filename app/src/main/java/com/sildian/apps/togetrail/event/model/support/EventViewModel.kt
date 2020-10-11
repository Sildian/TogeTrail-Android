package com.sildian.apps.togetrail.event.model.support

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Event related data
 ************************************************************************************************/

class EventViewModel : BaseObservableViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Logs**/
        private const val TAG = "EventViewModel"
    }

    /***********************************Exception handler***************************************/

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    /***********************************Data requester******************************************/

    private val eventDataRequester = EventDataRequester()

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

    fun currentUserIsAuthor(): Boolean =
        AuthRepository().getCurrentUser()?.uid == this.event?.authorId

    /**
     * Loads an event from the database in real time
     * @param eventId : the event's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadEventFromDatabaseRealTime(eventId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        this.queryRegistration?.remove()
        this.queryRegistration = eventDataRequester.loadEventFromDatabaseRealTime(eventId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    event = snapshot.toObject(Event::class.java)
                    notifyDataChanged()
                    Log.d(TAG, "Successfully loaded event from database")
                    successCallback?.invoke()
                }
                else if (e!=null) {
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

    fun loadEventFromDatabase(eventId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                event = async { eventDataRequester.loadEventFromDatabase(eventId) }.await()
                notifyDataChanged()
                Log.d(TAG, "Successfully loaded event from database")
                successCallback?.invoke()
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to load event from database : ${e.message}")
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Saves the event within the database
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun saveEventInDatabase(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.saveEventInDatabase(event, attachedTrails) }.join()
                Log.d(TAG, "Successfully saved event in database")
                successCallback?.invoke()
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to save event in database : ${e.message}")
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

    fun attachTrail(trail:Trail, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.attachTrail(event, trail) }.join()
                Log.d(TAG, "Successfully attached new trail to the event")
                successCallback?.invoke()
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to attach new trail to the event : ${e.message}")
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

    fun detachTrail(trail:Trail, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.detachTrail(event, trail) }.join()
                Log.d(TAG, "Successfully detached trail from the event")
                successCallback?.invoke()
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to detach trail from the event : ${e.message}")
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Registers the current user to the event
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun registerUserToEvent(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.registerUserToEvent(event) }.join()
                Log.d(TAG, "Successfully registered new user to the event")
                successCallback?.invoke()
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to register new user to the event : ${e.message}")
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Unregisters the current user from the event
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun unregisterUserFromEvent(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.unregisterUserFromEvent(event) }.join()
                Log.d(TAG, "Successfully unregistered user from the event")
                successCallback?.invoke()
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to unregister user from the event : ${e.message}")
                failureCallback?.invoke(e)
            }
        }
    }
}